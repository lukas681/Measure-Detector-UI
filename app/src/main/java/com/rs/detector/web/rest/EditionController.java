package com.rs.detector.web.rest;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.node.TextNode;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.service.editing.EditingService;
import com.rs.detector.web.api.EditionApiDelegate;
import com.rs.detector.web.api.model.ApiOrchEditionWithFileAsString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.scheduling.BackgroundJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        BackgroundJob.enqueue(() ->
                process(apiOrchEditionWithFileAsString, JobContext.Null));
        return EditionApiDelegate.super.addEdition(apiOrchEditionWithFileAsString);
    }

    public void process(ApiOrchEditionWithFileAsString apiOrchEditionWithFileAsString, JobContext jobContext) throws IOException {
        log.info("Object to be processed: " + apiOrchEditionWithFileAsString);

        var progressBar = jobContext.progressBar(100); // Let's say, we have 100% ...
        log.info("The PDF File Encoded: " + apiOrchEditionWithFileAsString.getPdfFile());
        var transformedText = parseDataFromBase64Encoded(apiOrchEditionWithFileAsString.getPdfFile());
        progressBar.setValue(10);
        var parsedEdition = parseEdition(apiOrchEditionWithFileAsString);
        try {
            editingService.uploadNewEdition(parsedEdition, PDDocument.load(transformedText));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO pass the progress!
        editingService.extractImagesFromPDF(parsedEdition, progressBar);
        progressBar.setValue(100); // Setting finished
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
