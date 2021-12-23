package com.rs.detector.service.measureDetection;

import com.rs.detector.service.editing.EditingService;
import com.rs.detector.service.editing.ScorePageService;
import com.rs.detector.service.editing.SimpleDataInitialization;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.context.JobContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeasureDetectorServiceTestIT extends SimpleDataInitialization {

    JobContext jobContext= JobContext.Null;

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
        editingService.extractImagesFromPDF(testEdition, jobContext);
        scorePageService.generatePageObjectIfNotExistent(testEdition);
        //__________________________________________________________

        // NOTE: This already works with the previously generated ong files!
        var res = editingService.runMeasureDetectionOnEdition(testEdition, 245);
        assertNotNull(res);
        assert(res.getMeasures().size()==4);
    }
}
