package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.MeasureBoxService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import com.rs.detector.web.api.model.MeasureBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
public class ScorePageService {

    private final Logger log = LoggerFactory.getLogger(ScorePageService.class);

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    MeasureBoxService measureBoxService;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    EditionService editionService;

    /**
     * This method will prepopulate the database with new page.
     *
     *iiii @param e
     * @return
     */
    public Flux<Page> generatePageObjectIfNotExistent(Edition e) throws PagesMightNotHaveBeenGeneratedException {
        var allGeneratedPages = editingFileManagementService.getAllGeneratedScorePageFiles(e);

        if (allGeneratedPages.size() == 0) {
            throw new PagesMightNotHaveBeenGeneratedException();
        }
        // Fetch, if there already have been some pages generated
        var pages = pageRepository
            .findAllByEditionId(e.getId())
            .collect(Collectors.toList())
            .block();

        for(var p:allGeneratedPages) {
            var fileNameToPageNr = EditingFileManagementService.convertFileNameToPageNumber(p);
            if(pages.stream().noneMatch(x -> x.getPageNr().equals(fileNameToPageNr))) {
               pages.add(new Page()
                   .imgFileReference(p)
                   .pageNr(fileNameToPageNr)
                   .edition(e)
               );
            } else {
                // TODO update Page
            }
        }
        sortAndFillNextLinks(pages);
        return pageRepository.saveAll(pages);
    }

    public List<Page> sortAndFillNextLinks(List<Page> pages) {
        logPages(pages);
        pages.sort(Comparator.comparing(Page::getPageNr));
        if(pages.size() == 1) {
            return pages;
        }
        for(int i = 0; i < pages.size()-1; i++) {
           pages.get(i).setNextPage(pages.get(i+1).getPageNr()); // Page number is a mantatory field
        }
        return pages;
    }

    private void logPages(List<Page> pages) {
        pages.forEach(x->log.debug(String.valueOf(x)));
    }

    public BufferedImage getBufferedImageFromPage(Edition e, long pageNr ) {
        assert(e != null);
//        return editingFileManagementService.getBufferedPage(e, );

        return null;
//        BufferedImage img = null;
//        try {
//            img = ImageIO.read(new File(""));
//        }
    }

    // TODO: How to handle recalculations? deleting everything. Ignoring it?
    public Flux<com.rs.detector.domain.MeasureBox> addMeasureDetectorResultBoxesToPage(ApiMeasureDetectorResult mdr,
                                                                                       Edition e, long pageNr) {
        var page = this.findPageByEditionIdAndPageNr(e, pageNr);
        Set<com.rs.detector.domain.MeasureBox> measureBoxes = new HashSet<>();

        // TODO what happens with old childs? Not being deleted automatically, right?
        mdr.getMeasures().forEach(measureBox->{
            measureBoxes.add(convertMeasureBoxDto(page, measureBox));
        });
       return measureBoxService.saveAll(new ArrayList<>(measureBoxes));
    }

    private com.rs.detector.domain.MeasureBox convertMeasureBoxDto(Page p, MeasureBox mb) {
        return new com.rs.detector.domain.MeasureBox()
            .page(p)
            .comment("")
            .measureCount(-1l)
            .lrx(valueOfCutDecimals(mb.getRight())) // Lower Right X
            .lry(valueOfCutDecimals(mb.getBottom())) // Lower Right Y
            .ulx(valueOfCutDecimals(mb.getLeft())) // Upper Left X
            .uly(valueOfCutDecimals(mb.getTop())); // Upper Left Y
    }

    // Cuts off all the (variable-lengthed) decimals and casts it to a long
    public static long valueOfCutDecimals(String pixelDecimal) {
        return (long)Double.parseDouble(pixelDecimal);
    }

    public Page findPageByEditionIdAndPageNr(Edition e, long pageNr) {
        var res = pageRepository.findAllByEditionIdAndPageNr(e.getId(),pageNr)
            .collect(Collectors.toList())
            .block();
        return (res.size()==0?null :res.get(0));
    }
}
