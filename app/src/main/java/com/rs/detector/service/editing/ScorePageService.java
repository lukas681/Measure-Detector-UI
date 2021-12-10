package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
public class ScorePageService {

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
     */
    public void generatePageObjectIfNotExistent(Edition e) throws PagesMightNotHaveBeenGeneratedException {
        var allGeneratedPageNames = editingFileManagementService.getAllGeneratedScorePageFiles(e);

        if(allGeneratedPageNames.size() == 0) {
            throw new PagesMightNotHaveBeenGeneratedException();
        }

        // Fetch, if there already have been some pages generated
        var generatedPages = pageRepository.findAllByEdition(e);

        for(var page:allGeneratedPageNames) {
            var test = new Page();
        }
//        pageRepository.saveAll();
    }
}
