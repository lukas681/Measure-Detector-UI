package com.rs.detector.service.editing;

import com.rs.detector.config.ApplicationProperties;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.service.ProjectService;
import com.rs.detector.service.util.FileUtils;
import com.sun.istack.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    FileUtils fileUtils;

    @Autowired
    private ProjectService projectService;

    private final ApplicationProperties applicationProperties;

    private Path basePathEditionPath; // To be filled in constructor.

    private int startIndex = 0;

    public EditingFileManagementService(ApplicationProperties applicationProperties) throws IOException {
        basePathEditionPath =  Path.of(applicationProperties.getEditionResourceBasePath());
        createNecessaryDirs();
        this.applicationProperties = applicationProperties;
    }

    /**
     * Overwrites
     * @param edition
     * @param pdfDocument
     */
    public void storePDFfile(@NotNull Project project, @NotNull Edition edition, @NotNull PDDocument pdfDocument) throws IOException {
        assert(edition.getpDFFileName() != null);
        assert(edition.getTitle() != null);

        String dir = constructPathFromProjectAndEdition(project, edition);
        Files.createDirectories(Path.of(dir));
        Path pdfPath = Path.of(dir + Path.of(edition.getpDFFileName()));

        if(Files.exists(pdfPath)) {
            log.warn("The file already exists and is being replaced!");
        }
        pdfDocument.save(new File(pdfPath.toString()));
    }

    /**
     * Loads the corresponding PDF file from a edition.
     *
     * TODO: Harden this method. Not save for penetration testing.
     * @param edition
     * @return
     */
    public PDDocument loadPdfFile(Project p, Edition edition) throws IOException {
        assert(edition.getpDFFileName() != null);
        assert(edition.getTitle() != null);

        String pdfPath = constructPathFromProjectAndEdition(p, edition)
            + File.separator + edition.getpDFFileName();

        if(!edition.getpDFFileName().endsWith(".pdf")) {
           pdfPath += ".pdf";
        }

        PDDocument document = PDDocument.load(new File(pdfPath));

        return document;
    }

    public void extractPagesFromEdition(Project p, Edition e) throws IOException {
        assert(e.getProjectId() != null);
        assert(p != null);
        PDDocument loadedEditionPdf = this.loadPdfFile(p, e);

        Path imageStorageLocation = Path.of(constructPathFromProjectAndEdition(p, e) + "/split");
        createOutputPathIfNotExistent(imageStorageLocation);

        fileUtils.convertPdf2Img2(loadedEditionPdf, imageStorageLocation, startIndex);
    }

    /**
     * Returns true, if the page was already generated and a image with the correct page number is stored in the target
     * location
     * /split/_
     * @param p
     * @param e
     * @param pageNr
     * @return
     */
    public boolean isPageExistent(Project p, Edition e, int pageNr) {
        return Files.exists(
            Path.of(constructPathFromProjectAndEdition(p, e) + "/split/_" + pageNr)
        );
    }


    private void createOutputPathIfNotExistent(Path imageStorageLocation) throws IOException {
        if(!Files.exists(imageStorageLocation)) {
            Files.createDirectories(imageStorageLocation);
        }
    }


// TODO Decide if necessary public PDDocument deletePdfFile(Edition edition) throws IOException {

    /**
     * Checks, whether the PDF file provided is encrypted
      * @param pdf
     * @return
     */
    public boolean checkPDFEncrypted(PDDocument pdf) {
        return pdf.isEncrypted();
    }

    public String getBasePathEdition() {
        return basePathEditionPath.toString();
    }

    public void createNecessaryDirs () throws IOException {
        if(!Files.exists(basePathEditionPath)){
            Files.createDirectories(basePathEditionPath);
        }
    }
    public String constructPathFromProjectAndEdition(Project p, Edition e) {
        return basePathEditionPath.toString() + File.separator + p.getId() +
                File.separator + e.getId() + "/";
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}
