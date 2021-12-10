package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Comparator;
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
        var allGeneratedPageNames = editingFileManagementService.getAllGeneratedScorePageFiles(e);

        if (allGeneratedPageNames.size() == 0) {
            throw new PagesMightNotHaveBeenGeneratedException();
        }
        // Fetch, if there already have been some pages generated
        var pages = pageRepository
            .findAllByEditionId(e.getId())
            .collect(Collectors.toList())
            .block();

        for(var p:allGeneratedPageNames) {
            var filenameToPageNumber = convertFileNameToPageNumber(p);

            if(pages.stream().filter(x->x.getPageNr().equals(filenameToPageNumber)).count() == 0) {
               pages.add(new Page()
                   .imgFileReference(p)
                   .pageNr(filenameToPageNumber)
                   .edition(e)
               );
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

    private Long convertFileNameToPageNumber(String p) {
        p = StringUtils.stripFilenameExtension(p);
        return Long.parseLong(
            p.replace("_","")
        );
    }
}
