package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.repository.EditionSqlHelper;
import com.rs.detector.service.EditionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     *
     */
    public void processEdition() {

    }
}
