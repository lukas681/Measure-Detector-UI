package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.repository.ProjectRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "application.editionResourceBasePath=build/res",
    "application.imageSplitDPI=400"
})
class EditingServiceTest {

    private Project testProject;
    private Edition testEdition;

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    EditingService editingService;

    @BeforeEach
    public void setup() {
        projectRepository.deleteAll().block();
        editionRepository.deleteAll().block();


        testProject = new Project()
            .name("TestProject")
            .composer("Richi Strauß");
        projectRepository.save(testProject).block();

        testEdition = new Edition()
            .title("testTitle")
            .pDFFileName("testEdition.pdf")
            .project(testProject);
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
    void UploadAndsplitPDF() throws IOException {
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));

        // We do not want to generate everything of the 248 example pages ... This is something for the user only :)
        editingService.getEditingFileManagementService().setStartIndex(243);

        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);
    }
}
