package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import com.rs.detector.domain.Project;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.ProjectService;
import com.rs.detector.service.measureDetection.MeasureDetectorService;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
public class EditingService {

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    MeasureDetectorService measureDetectorService;

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    ProjectService projectService;

    @Autowired
    EditionService editionService;

    /**
     * Provides a conventient way to upload a new Edition with a corresponding PDF file which will later be splitted
     * and analysed by the Measure Detector.
     * @param e The Edition
     * @param pdfFile A PDF file with the music sheet
     */
    public void uploadNewEdition(Edition e, PDDocument pdfFile) throws IOException {
        editionService.save(e).block();

        editingFileManagementService.storePDFfile(e, pdfFile);

    }

    /**
     * Extracts  the pages as images from an Edition
     * @param e
     */
    public void extractImagesFromPDF(Edition e) throws IOException {
        editingFileManagementService.extractPagesFromEdition(e);
    }

    // TODO Catch Exception
    public void triggerMeasureDetectionFull(@NotNull Edition e) throws IOException {
        var allGeneratedAvailableScorePages = editingFileManagementService.getAllGeneratedScorePageFilesAsPageNr(e);
        for(var pageNr: allGeneratedAvailableScorePages) {
            var measureDetectorResult = runMeasureDetectionOnPage(e, pageNr);
//            scorePageService.updatePage();
        }
    }

    public ApiMeasureDetectorResult runMeasureDetectionOnPage(@NotNull Edition e, long pageNr) throws IOException {
        // TODO Implement
        var bufferedImage = scorePageService.getBufferedImageFromPage(e, pageNr);
        File f = new File("asd");
        return measureDetectorService.process(f);
    }

    public EditingFileManagementService getEditingFileManagementService() {
        return this.editingFileManagementService;
    }
}
