package com.rs.detector.web.rest;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.node.TextNode;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.service.editing.EditingService;
import com.rs.detector.web.api.EditionApi;
import com.rs.detector.web.api.EditionApiDelegate;
import com.rs.detector.web.api.model.ApiOrchEditionWithFile;
import com.rs.detector.web.api.model.ApiOrchEditionWithFileAsString;
import io.swagger.annotations.ApiParam;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

@Service
public class EditionController implements EditionApiDelegate {

    private final Logger log = LoggerFactory.getLogger(EditionController.class);

    @Autowired
    EditingService editingService;

    @Override
    // TODO need rework.  The problem is that Jackson can not decode resources OOTB.
    public ResponseEntity<Void> addEdition(ApiOrchEditionWithFileAsString apiOrchEditionWithFileAsString)  {
        log.info("Object to be processed: " + apiOrchEditionWithFileAsString);
        log.info("The PDF File Encoded: " + apiOrchEditionWithFileAsString.getPdfFile());
        var transformedText = parseDataFromBase64Encoded(apiOrchEditionWithFileAsString.getPdfFile());
        var parsedEdition = parseEdition(apiOrchEditionWithFileAsString);

        // TODO make a background task for that.
        try {
            editingService.uploadNewEdition(parsedEdition, PDDocument.load(transformedText));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return EditionApiDelegate.super.addEdition(apiOrchEditionWithFileAsString);
    }

    private Edition parseEdition(ApiOrchEditionWithFileAsString apiOrchEditionWithFileAsString) {
        var edition = new Edition()
            .title(apiOrchEditionWithFileAsString.getTitle())
            .type(EditionType.valueOf(apiOrchEditionWithFileAsString.getType()))
            .createdDate(Instant.parse(apiOrchEditionWithFileAsString.getCreatedDate()))
            .pDFFileName(apiOrchEditionWithFileAsString.getpDFFileName())
            .description(apiOrchEditionWithFileAsString.getDescription());
        edition.setProjectId(apiOrchEditionWithFileAsString.getProjectId());
        return edition;
    }

    public byte[] parseDataFromBase64Encoded(String s) {
        TextNode n = new TextNode(s.replace("data:application/pdf;base64,",""));
        byte[] data = null;
        try {
            data = n.getBinaryValue(Base64Variants.MIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
