package com.rs.detector.service.editing;

import com.rs.detector.config.ApplicationProperties;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"application.editionResourceBasePath=build/res"})
class EditingFileManagementServiceTest {

    Edition testEdition;
    Project testProject;

    String sep = File.separator;

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    ApplicationProperties applicationProperties;

    @BeforeEach
    public void setup() {

        testProject = new Project()
            .name("TestProject")
            .composer("Richi Strauß");

        testEdition = new Edition()
            .title("testTitle")
            .pDFFileName("testEdition.pdf")
        .project(testProject);
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
    void uploadNewEdition() {

    }

    @Test
    void processEdition() {
    }
}
