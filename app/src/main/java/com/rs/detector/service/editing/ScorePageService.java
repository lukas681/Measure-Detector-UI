package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import com.rs.detector.domain.util.utils;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.MeasureBoxService;
import com.rs.detector.service.PageService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import com.rs.detector.web.api.model.MeasureBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
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
    PageService pageService;

    @Autowired
    MeasureBoxService measureBoxService;

    @Autowired
    PageRepository pageRepository;

    @Autowired
    EditionService editionService;

    /**
     * This method will prepopulate the database with new page.
     * <p>
     *
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
            .collect(Collectors.toList()).share()
            .block();

        for (var p : allGeneratedPages) {
            var fileNameToPageNr = EditingFileManagementService.convertFileNameToPageNumber(p);
            if (pages.stream().noneMatch(x -> x.getPageNr().equals(fileNameToPageNr))) {
                pages.add(new Page()
                    .imgFileReference(p)
                    .pageNr(Long.valueOf(fileNameToPageNr))
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
        if (pages.size() == 1) {
            return pages;
        }
        for (int i = 0; i < pages.size() - 1; i++) {
            pages.get(i).setNextPage(pages.get(i + 1).getPageNr()); // Page number is a mantatory field
        }
        return pages;
    }

    private void logPages(List<Page> pages) {
        pages.forEach(x -> log.debug(String.valueOf(x)));
    }

    public Flux<com.rs.detector.domain.MeasureBox> addMeasureDetectorResultBoxesToPage(ApiMeasureDetectorResult mdr,
                                                                                       Edition e, long pageNr) {
        var page = this.findPageByEditionIdAndPageNr(e, pageNr);
        Set<com.rs.detector.domain.MeasureBox> measureBoxes = new HashSet<>();

        // TODO what happens with old childs? Not being deleted automatically, right?
        mdr.getMeasures().forEach(measureBox -> {
            measureBoxes.add(convertMeasureBoxDto(page, measureBox, -1));
        });
        return measureBoxService.saveAll(new ArrayList<>(measureBoxes));
    }

    private com.rs.detector.domain.MeasureBox convertMeasureBoxDto(Page p, MeasureBox mb, long measureCount) {
        return new com.rs.detector.domain.MeasureBox()
            .page(p)
            .comment("Generated by Deep Optical Measure Detector")
            .measureCount(measureCount)
            .lrx(valueOfCutDecimals(mb.getRight())) // Lower Right X
            .lry(valueOfCutDecimals(mb.getBottom())) // Lower Right Y
            .ulx(valueOfCutDecimals(mb.getLeft())) // Upper Left X
            .uly(valueOfCutDecimals(mb.getTop())); // Upper Left Y
    }

    // Cuts off all the (variable-lengthed) decimals and casts it to a long
    public static long valueOfCutDecimals(String pixelDecimal) {
        return (long) Double.parseDouble(pixelDecimal);
    }

    public Page findPageByEditionIdAndPageNr(Edition e, long pageNr) {
        var res = pageRepository.findAllByEditionIdAndPageNr(e.getId(), pageNr)
            .collect(Collectors.toList())
            .block();
        return (res.size() == 0 ? null : res.get(0));
    }

    /**
     * Takes a new Measure Detector Run (@{@link ApiMeasureDetectorResult}, deletes the attached boxes on the page
     * and replaces them. This is probably the savest way to work with changes and not get messed-up.
     * @param page
     * @param measureDetectorResult
     * @return
     */
    public Flux<com.rs.detector.domain.MeasureBox> updatePageMeasureBoxesWithMDResult(Page page, ApiMeasureDetectorResult measureDetectorResult) {
        assert (page.getId() != null);
        pageService.deleteAllMeasureBoxes(page.getId()).collectList().toProcessor().block();

        var measureBoxes = new HashSet<com.rs.detector.domain.MeasureBox >();
        var measures = measureDetectorResult.getMeasures();
        for(int i = 0; i < measures.size(); i++) {
            measureBoxes.add(convertMeasureBoxDto(page, measures.get(i), i));
        }
        log.debug("Now Replacing the old Measure Boxes");
        return measureBoxService.saveAll(new ArrayList<>(measureBoxes));
    }

    public long getNumberOfPages(Long editionID) {
        return pageRepository.findAllByEditionId(editionID)
            .collectList()
            .toProcessor()
            .block()
            .size();
    }
    public BufferedImage addMeasureBoxesToBufferedImage(BufferedImage img, Page p) {
        var measureBoxes =   measureBoxService.findAllByPageId(p.getId())
            .collectList()
            .toProcessor()
            .block();
        for(var mb: measureBoxes) {
            log.debug("Adding MeasureBox to image: " + mb.getMeasureCount());
            img = addSingleMeasureBoxToImage(img, mb, p.getMeasureNumberOffset());
        }
        return img;
    }

    public BufferedImage addSingleMeasureBoxToImage(BufferedImage img, com.rs.detector.domain.MeasureBox mb,
                                                    Long offset) {
        Long[] mbCoordinates = utils.convertToXYWH(mb);
        // Adding Rectangle
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.getHSBColor((float) (Math.random()*359+1d), (float) Math.min(Math.random() +.5, 1),1F)); //
        // Generate a
        // random
        // saturation
        g2d.setComposite( AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.1f));
        g2d.fillRect(Math.toIntExact(mbCoordinates[0]),
            Math.toIntExact(mbCoordinates[1]),
            Math.toIntExact(mbCoordinates[2]),
            Math.toIntExact(mbCoordinates[3]));

        // Adding Measure Number
        int fontSize = img.getHeight() / 40;
        g2d = img.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Purisa", Font.BOLD, fontSize));
        g2d.setComposite( AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.9f));
        if(mb.getMeasureCount() != null) {
            offset = offset == null?0:offset;
            g2d.drawString(mb.getMeasureCount() + offset + "", mbCoordinates[0], mbCoordinates[1] + 10);
        }
        return img;
    }

    static <E> E getRandomSetElement(Set<E> set) {
        return set.stream()
            .skip(new Random()
                .nextInt(set.size()))
            .findFirst().orElse(null);
    }
}
