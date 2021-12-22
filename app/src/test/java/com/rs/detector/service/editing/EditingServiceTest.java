package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "application.editionResourceBasePath=build/res",
    "application.imageSplitDPI=400"
})
class EditingServiceTest extends SimpleDataInitialization {

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    EditingService editingService;

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    void uploadNewEdition() throws IOException {
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        // Should create a new object and stores the file
       editingService.uploadNewEdition(testEdition, pdf);

       // Testing the creation
       var res = editionRepository.findAll().collectList().block();
        System.out.println(res.get(0).getId());
       assert(res.get(0).getId().equals(1l));

        var projects = projectRepository.findAll().collectList().block();
       assert(projects.size()>0);
    }

    @Test
    void uploadCompletelyNewEdition() throws IOException {
        Edition newEdition = new Edition()
            .id(null)
            .project(null)
            .pages(new HashSet<>())
            .createdDate(Instant.now())
            .description("This is a Test")
            .type(EditionType.valueOf("SCORE"))
            .title("Testtitle")
            .pDFFileName("Testfile");
        newEdition.setProjectId(1l);

        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        // Should create a new object and stores the file
        editingService.uploadNewEdition(newEdition, pdf);

        // Testing the creation
        var res = editionRepository.findAll().collectList().block();
        System.out.println(res.get(0).getId());
        assert(res.get(0).getId().equals(1l));

        var projects = projectRepository.findAll().collectList().block();
        assert(projects.size()>0);
    }

    @Test
    void UploadAndsplitPDF() throws IOException {
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));

        // We do not want to generate everything of the 248 example pages ... This is something for the user only :)
        editingService.getEditingFileManagementService().setStartIndex(243);

        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);
    }

    @Test
    void runMeasureDetectionOnPage() throws IOException, PagesMightNotHaveBeenGeneratedException {
        // Setup :)
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        editingService.getEditingFileManagementService().setStartIndex(243);

        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);
        scorePageService.generatePageObjectIfNotExistent(testEdition).blockLast();
        //__________________________________________________________

        var res = editingService.runMeasureDetectionOnPage(testEdition, 243);
        assertNotNull(res);


    }

    @Test
    void runFullMeasureDetectionOverEdition() throws IOException, PagesMightNotHaveBeenGeneratedException {
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        editingService.getEditingFileManagementService().setStartIndex(245);
        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);
        scorePageService.generatePageObjectIfNotExistent(testEdition).blockLast();

        editingService.runFullMeasureDetectionOverEdition(testEdition);

        var res = pageRepository.findAllByEditionId(testEdition.getId()).collectList().block();

    }
}
