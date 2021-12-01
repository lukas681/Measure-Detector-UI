package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
@Transactional
public class EditingService {

    /**
     * Provides a conventient way to upload a new Edition with a corresponding PDF file which will later be splitted
     * and analysed by the Measure Detector.
     * @param e The Edition
     * @param pdfFile A PDF file with the music sheet
     */
    public void uploadRawEdition(Edition e, File pdfFile) {

    }

    /**
     *
     */
    public void processEdition() {

    }
}
