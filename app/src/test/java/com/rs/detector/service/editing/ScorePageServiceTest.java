package com.rs.detector.service.editing;

import com.rs.detector.domain.Page;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScorePageServiceTest extends SimpleDataInitialization {

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    EditingService editingService;

    @BeforeEach
    void init() {
        super.setup();

    }

    @Test
    void generatePageObjectIfNotExistent() throws PagesMightNotHaveBeenGeneratedException, IOException {
        var pdf = PDDocument
            .load(new File("src/test/resources/scores/aegyptische-helena.pdf")
            );

        // We do not want to generate everything of the 248 example pages ... This is something for the user only :)
        editingService.getEditingFileManagementService().setStartIndex(243);

        // This already handles the database storage
        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);

        scorePageService.generatePageObjectIfNotExistent(testEdition);

        StepVerifier
            .create(scorePageService.generatePageObjectIfNotExistent(testEdition))
            .expectNextMatches((m->m.getEditionId() == 245l))
            .then( () -> System.out.println("Test"))
            .expectComplete()
            .verify();
    }
}
