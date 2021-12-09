package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.service.EditionService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This service projects the process of editing music. Therefore, it provides methods to create new Editins along with the corresponding scanned sheets.
 */
@Service
public class ScorePageService {

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    EditionService editionService;


}
