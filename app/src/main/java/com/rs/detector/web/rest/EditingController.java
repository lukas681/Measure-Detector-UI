package com.rs.detector.web.rest;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.node.TextNode;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.service.EditionService;
import com.rs.detector.service.editing.EditingService;
import com.rs.detector.service.editing.ScorePageService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.service.util.FileUtilService;
import com.rs.detector.web.api.EditionApiDelegate;
import com.rs.detector.web.api.model.ApiOrchEditionWithFileAsString;
import com.rs.detector.web.api.model.ApiOrchMeasureBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.BackgroundJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tech.jhipster.config.JHipsterDefaults;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EditingController implements EditionApiDelegate {

    @Autowired
    EditingService editingService;


    private final Logger log = LoggerFactory.getLogger(EditingController.class);


    @Override
    public ResponseEntity<List<ApiOrchMeasureBox>> getMeasureBoxesByEditionIdAndPageNr(Integer editionID, Integer pageNr) {
        var res =  editingService.getMeasureBoxesbyEditionIDandPageNr(editionID, Long.valueOf(pageNr));

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                    .body(res);
    }

    @Override
    public ResponseEntity<Resource> getPageByPageNrAndEditionID(Integer editionID, Integer pageNr) {
        try {
            var headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            var resource =  editingService.getPageResourceToEditionAndPageNr(editionID, Long.valueOf(pageNr));
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        } catch (FileNotFoundException | MalformedURLException e) {
            e.printStackTrace();
        }
        return (ResponseEntity<Resource>) ResponseEntity.badRequest();
    }

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    EditionService editionService;

    @Override
    public ResponseEntity<Void> addEdition(ApiOrchEditionWithFileAsString apiOrchEditionWithFileAsString)  {
        if(apiOrchEditionWithFileAsString.getPdfFile() == null) {
            return ResponseEntity.badRequest().build();
        }
        BackgroundJob.enqueue(UUID.randomUUID(), () ->
                process(apiOrchEditionWithFileAsString, JobContext.Null));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> runFullMeasureDetectionByEditionId(Integer id) {
        if(id != null) {
            BackgroundJob.enqueue(UUID.randomUUID(), () -> processFullDetection(id, JobContext.Null));
        }
        return ResponseEntity.ok("The Job was successfully scheduled and now being processed in the background.");
//        return EditionApiDelegate.super.runFullMeasureDetectionByEditionId(id);
    }

    public void processFullDetection(Integer id, JobContext jobContext) {
            try {
                var e = editionService.findOne(Long.valueOf(id)).share().block();
                assert(e != null);
                scorePageService.generatePageObjectIfNotExistent(e)
                    .collectList()
                    .toProcessor()
                    .block();
                editingService.runFullMeasureDetectionOverEdition(e, jobContext);
            } catch (IOException e) {
                log("The following error has occured: " + e, jobContext);
                log(e.fillInStackTrace().getMessage(), jobContext );
            } catch (PagesMightNotHaveBeenGeneratedException e) {
                log("The pages might not have been generated", jobContext);
                log(e.fillInStackTrace().getMessage(), jobContext);
            }
    }

    /**
     * TODO We are currently limited to the string size.
     * @param apiOrchEditionWithFileAsString
     * @param jobContext
     * @throws IOException
     */
    @Job(name = "Uploading a new Edition.")
    public void process(ApiOrchEditionWithFileAsString apiOrchEditionWithFileAsString, JobContext jobContext) throws IOException, PagesMightNotHaveBeenGeneratedException {
        log("Starting. objects to be processed", jobContext);
        var progressBar = jobContext.progressBar(100); // Let's say, we have 100% ...
        var transformedText = parseDataFromBase64Encoded(apiOrchEditionWithFileAsString.getPdfFile()); // TODO switch
        // that away from a base64 encoded string. Kills java heap memory.
        log("Successfully Parsed incoming PDF file!", jobContext);
        progressBar.setValue(10);
        var parsedEdition = parseEdition(apiOrchEditionWithFileAsString);
        try {
            log("Creating a new Edition", jobContext);
            editingService.uploadNewEdition(parsedEdition, PDDocument.load(transformedText));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editingService.extractImagesFromPDF(parsedEdition, jobContext);
        scorePageService.generatePageObjectIfNotExistent(parsedEdition);
        progressBar.setValue(100); // Setting finished
        log("Finished Job", jobContext);
    }

    private void log(String message, JobContext jobContext) {
        log.info(message);
        jobContext.logger().info(message);

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

    @Override
    public ResponseEntity<Long> getMeasureBoxesOffsetByEditionIdAndPageNr(Integer editionID, Integer pageNr) {
        var offset = editingService.getMeasureBoxesOffsetbyEditionIDandPageNr(editionID, Long.valueOf(pageNr));
        log.debug("The Calculated Offset fot the Page " + pageNr + " and edition "+ editionID + " is " + offset);

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(offset);
        }

    @Override
    public ResponseEntity<Void> saveMeasureBoxesByEditionIdAndPageNr(Integer editionID, Integer pageNr,
                                                                     List<ApiOrchMeasureBox> apiOrchMeasureBox) {
        editingService.saveMeasureBoxesbyEditionIdAndPageNr(editionID, pageNr, apiOrchMeasureBox);
        editingService.recalculatePageOffsets(editionID);
        return ResponseEntity.ok().build();
        // TODO Catch errors.
    }


    @Override
    public ResponseEntity<Long> getNumberOfPagesToEdition(Long editionID) {
        var numberOfPages = scorePageService.getNumberOfPages(editionID);
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(numberOfPages);
    }

    @Override
    public ResponseEntity<Resource> getAnnotatedPDF(Long editionID) {
        InputStreamResource file = null;

        var edition = editionService.findOne(editionID)
            .toProcessor()
            .block();
        if(edition == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            editingService.createPdfWithMeasures(edition);
             file = new InputStreamResource(editingService.getGeneratedPDFFile(edition));
        } catch (IOException | PagesMightNotHaveBeenGeneratedException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline;attachment; filename="+ edition.getpDFFileName())
            .contentType(MediaType.APPLICATION_PDF)
            .body(file);
    }
}
