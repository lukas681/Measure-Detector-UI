package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.ProjectService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
public class EditingService {

    @Autowired
    EditingFileManagementService editingFileManagementService;

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

        System.out.println(e.getProjectId());
        editionService.save(e).block();

        editingFileManagementService.storePDFfile(e.getProject(), e, pdfFile);

    }

    /**
     * Extracts  the pages as images from an Edition
     * @param e
     */
    public void extractImagesFromPDF(Edition e) throws IOException {
        // Get project
        var relatedProject = projectService.findOne(e.getProjectId())
            .block();

        editingFileManagementService.extractPagesFromEdition(relatedProject, e);
    }


    public void triggerMeasureDetection(Edition e) {

    }

    public EditingFileManagementService getEditingFileManagementService() {
        return this.editingFileManagementService;
    }
}
