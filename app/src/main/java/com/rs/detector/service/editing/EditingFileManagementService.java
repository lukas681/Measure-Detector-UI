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
    public void storePDFfile(@NotNull Edition edition, @NotNull PDDocument pdfDocument) throws IOException {
        assert(edition.getpDFFileName() != null);
        assert(edition.getTitle() != null);

        String dir = basePathEditionPath.toString() + File.separator + edition.getTitle() + File.separator;
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
    public PDDocument loadPdfFile(Edition edition) throws IOException {
        // TODO Add project reference!
        assert(edition.getpDFFileName() != null);
        assert(edition.getTitle() != null);

        String pdfPath = constructPathFronProjectAndEdition(null, edition) + File.separator + edition.getpDFFileName();

        if(!edition.getpDFFileName().endsWith(".pdf")) {
           pdfPath += ".pdf";
        }
        PDDocument document = PDDocument.load(new File(pdfPath));

        return document;
    }
    public void extractImagesFromPDF(Edition e) throws IOException {
        assert(e.getProjectId() != null);

        var parentProject = projectService.findOne(e.getProjectId()).block();
        System.out.println(parentProject);

        PDDocument loadedEditionPdf = this.loadPdfFile(e);

        Path imageStorageLocation = Path.of(constructPathFronProjectAndEdition(null, e) + "/split");
        createOutputPathIfNotExistent(imageStorageLocation);

        fileUtils.convertPdf2Img2(loadedEditionPdf, imageStorageLocation, startIndex);

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
    public String constructPathFronProjectAndEdition(Project p, Edition e) {
        // TODO add project here.
        return basePathEditionPath.toString() +
                File.separator + e.getTitle();
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }
}
