package com.rs.detector.service.measureDetection;

import com.rs.detector.service.editing.EditingService;
import com.rs.detector.service.editing.ScorePageService;
import com.rs.detector.service.editing.SimpleDataInitialization;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeasureDetectorServiceTestIT extends SimpleDataInitialization {

    @Autowired
    MeasureDetectorService measureDetectorService;

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    EditingService editingService;

    @Override
    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    /**
     * Calling the "Real" measure detector
     */
    public void callMeasureDetectorAndCreateEverything() throws IOException,
        PagesMightNotHaveBeenGeneratedException {
        var pdf = PDDocument.load(new File("src/test/resources/scores/aegyptische-helena.pdf"));
        editingService.getEditingFileManagementService().setStartIndex(243);

        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);
        scorePageService.generatePageObjectIfNotExistent(testEdition);
        //__________________________________________________________

        var res = editingService.runMeasureDetectionOnPage(testEdition, 245);
        assertNotNull(res);
    }
}
