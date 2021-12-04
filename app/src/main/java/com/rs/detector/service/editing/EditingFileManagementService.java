package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.sun.istack.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
/**
 * Provides a convenient way to synchronize the database with a local file storage keeping the edition files (PNG and
 * PDF...)
 */
public class EditingFileManagementService {

    private final Logger log = LoggerFactory.getLogger(EditingFileManagementService.class);

    public EditingFileManagementService () throws IOException {
        createNecessaryDirs();
    }

    // TODO outsource property
    private final Path basePathEdition = Path.of("res" + File.separator);

    /**
     * Overwrites
     * @param edition
     * @param pdfDocument
     */
    public void storePDFfile(@NotNull Edition edition, @NotNull PDDocument pdfDocument) throws IOException {
        assert(edition.getpDFFileName() != null);
        assert(edition.getTitle() != null);

        String dir = basePathEdition + File.separator + edition.getTitle() + File.separator;
        Files.createDirectories(Path.of(dir));
        Path pdfPath = Path.of(dir + Path.of(edition.getpDFFileName()));

        if(Files.exists(pdfPath)) {
            log.warn("The file already exists and is being replaced!");
        }
        pdfDocument.save(new File(pdfPath.toString()));
    }

    /**
     * Loads the corresponding PDF file from a edition
     * @param edition
     * @return
     */
    public PDDocument loadPdfFile(Edition edition) throws IOException {
        assert(edition.getpDFFileName() != null);

        String pdfPath = basePathEdition + File.separator +  edition.getpDFFileName();
        // TODO Maybe unneccesary.
        if(!edition.getpDFFileName().endsWith(".pdf")) {
           pdfPath += ".pdf";
        }
        PDDocument document = PDDocument.load(new File(pdfPath));

        return document;
    }

    /**
     * Checks, whether the PDF file provided is encrypted
      * @param pdf
     * @return
     */
    public boolean checkPDFEncrypted(PDDocument pdf) {
        return pdf.isEncrypted();
    }

    public String getBasePathEdition() {
        return basePathEdition.toString();
    }

    public void createNecessaryDirs () throws IOException {
        if(!Files.exists(basePathEdition)){
            Files.createDirectories(basePathEdition);
        }
    }
}
