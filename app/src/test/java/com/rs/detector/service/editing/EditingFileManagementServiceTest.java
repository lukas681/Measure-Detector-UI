package com.rs.detector.service.editing;

import com.rs.detector.config.ApplicationProperties;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.scheduling.BackgroundJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"application.editionResourceBasePath=build/res"})
class EditingFileManagementServiceTest extends SimpleDataInitialization {


    String sep = File.separator;
    String fileFormat = ".png"; // TODO Outsource in config

    JobContext jobContext = null;

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    ApplicationProperties applicationProperties;

    @BeforeEach
    public void setup() {
       super.setup();
    }

    @Test
    void storePDFfileWithNullDocument() {
        try {
            editingFileManagementService.storePDFfile(testEdition, null);
        }catch(Exception e) {
            return;
        }
        assert(false);
    }

    @Test
    void storeAndLoadPDFfile() throws IOException {
        String basePath = editingFileManagementService.getBasePathEdition();

        // Storing
        PDDocument pdf = new PDDocument();
        editingFileManagementService.storePDFfile(testEdition, pdf);
        Path fileLocation = Path.of(basePath + sep + "testTitle" + sep + "testEdition.pdf");
        System.out.println(fileLocation.toAbsolutePath().toString());
        assert(Files.exists(fileLocation));

        // Now Loading
        PDDocument doc = editingFileManagementService.loadPdfFile(testEdition);
        assertNotNull(doc);

    }

    @Test
    void basePathLoadedCorrectly() {
        System.out.println(applicationProperties.getEditionResourceBasePath());
       assert(applicationProperties.getEditionResourceBasePath().equals("build/res"));
    }

    @Test
    void PageExistingAfterSplit() throws IOException {
        String basePath = editingFileManagementService.getBasePathEdition();


        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );
        editingFileManagementService.storePDFfile(testEdition, pdf);

        Path fileLocation = Path.of(basePath
            + sep + "1"
            + sep + "1"
            + sep + "aegyptische-helena.pdf");

        System.out.println(fileLocation.toAbsolutePath().toString());

//        var parentProject = projectService.findOne(e.getProjectId()).block();

        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);
        assert(Files.exists(fileLocation));

        pdf.close();
    }
    @Test
    void fileExistentMethodWorkingAndSplittingProcess() throws IOException {
        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );

        editingFileManagementService.storePDFfile(testEdition, pdf);
        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);
        assert(editingFileManagementService.isPageExistent(testEdition, 245)) ;
        pdf.close();
    }

    @Test
    void testGetAllFileNamesAfterGeneration() throws IOException {
        var assertedPageNames = new ArrayList<> (){
            {
                add("_243.png");
                add("_244.png");
                add("_245.png");
                add("_246.png");
                add("_247.png");
            }
        };

        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );

        editingFileManagementService.storePDFfile(testEdition, pdf);
        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);

        var res =
            editingFileManagementService.getAllGeneratedScorePageFiles(testEdition);
        System.out.println(res);
        assert(assertedPageNames.containsAll(res));

    }

    @Test
    void loadPageResource() throws IOException, PagesMightNotHaveBeenGeneratedException {
        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );

        editingFileManagementService.storePDFfile(testEdition, pdf);
        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);

        var resource = editingFileManagementService.getPage(testEdition, 245);
        assertNotNull(resource);
    }

    @Test
    void loadPage() throws IOException, PagesMightNotHaveBeenGeneratedException {
        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );

        editingFileManagementService.storePDFfile(testEdition, pdf);
        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);

        var bufferedImage = editingFileManagementService.loadPage(testEdition, 245);
        assertNotNull(bufferedImage);
    }

    @Test
    void deleteEditionFiles() throws IOException, PagesMightNotHaveBeenGeneratedException {
       // Create a new edition
        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );
        editingFileManagementService.storePDFfile(testEdition, pdf);
        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);

        Path p = Path.of(editingFileManagementService.constructPathFromEdition(testEdition));
        System.out.println(p);
        System.out.println(Files.isDirectory(p));
        assert(Files.exists(p)
        );
        editingFileManagementService.deleteEditionFiles(testEdition);

        assert(!Files.exists(p));
    }

    @Test
    void createMergedPDFFile() throws IOException {
        var pdf = PDDocument.load(
            new File("src/test/resources/scores/aegyptische-helena.pdf")
        );
        editingFileManagementService.storePDFfile(testEdition, pdf);
        editingFileManagementService.setStartIndex(245);
        editingFileManagementService.extractPagesFromEdition(testEdition, jobContext);

        editingFileManagementService.combineImagesIntoPDF("src/test/resources/scores/test.pdf",
            editingFileManagementService.constructPathForSplittedPagesFromEdition(testEdition));
        // Path p = Path.of(editingFileManagementService.constructPathFromEdition(testEdition));
    }

}
