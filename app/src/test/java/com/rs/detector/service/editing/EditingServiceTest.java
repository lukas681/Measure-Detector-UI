package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.BackgroundJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "application.editionResourceBasePath=build/res",
    "application.imageSplitDPI=400"
})
class EditingServiceTest extends SimpleDataInitialization {

    JobContext jobContext= JobContext.Null;

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
        editingService.extractImagesFromPDF(testEdition, jobContext);
    }

    @Test
    void runMeasureDetectionOnPage() throws IOException, PagesMightNotHaveBeenGeneratedException {
        // Setup :)
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        editingService.getEditingFileManagementService().setStartIndex(243);

        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition, jobContext);
        scorePageService.generatePageObjectIfNotExistent(testEdition).blockLast();
        //__________________________________________________________

        var res = editingService.runMeasureDetectionOnEdition(testEdition, 243);
        assertNotNull(res);


    }

    @Test
    void runFullMeasureDetectionOverEdition() throws IOException, PagesMightNotHaveBeenGeneratedException {
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        editingService.getEditingFileManagementService().setStartIndex(245);
        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition, jobContext);
        scorePageService.generatePageObjectIfNotExistent(testEdition).blockLast();

        editingService.runFullMeasureDetectionOverEdition(testEdition, null);

        var res = pageRepository.findAllByEditionId(testEdition.getId()).collectList().block();

    }
    @Test
    void testGetCorrectMeasureBoxesForPageNrAndEdition() throws IOException, PagesMightNotHaveBeenGeneratedException {
        var comp =
            measureBoxRepository.findByPageId(testPage.getId())
                .collectList()
                .toProcessor().block()
                .stream().map(EditingService::MeasureBoxToApiOrch)
                .collect(Collectors.toList());
        // Asuming this default method is correct.

        assert (measureBoxRepository.findByPageId(testPage.getId()).collectList().toProcessor().block().size() == 3);

        var res =
            editingService.getMeasureBoxesbyEditionIDandPageNr(Math.toIntExact(testEdition.getId()), 245l);
        assert(res.size() == 3);
        assert(res.containsAll(comp));


        }
    @Test
    void bug() throws InterruptedException {
        Integer id = 1;
        BackgroundJob.enqueue(() -> methodToCall(id, JobContext.Null));
            Thread.sleep(1000);
    }
    public void methodToCall(int id, JobContext jobContext) {
        System.out.println("ERROR");
    }

}
