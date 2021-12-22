package com.rs.detector.service.editing;

import com.rs.detector.config.ApplicationProperties;
import com.rs.detector.domain.Edition;
import com.rs.detector.service.ProjectService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.service.util.FileUtilService;
import com.sun.istack.NotNull;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
/**
 * Provides a convenient way to synchronize the database with a local file storage keeping the edition files (PNG and
 * PDF...)
 */
public class EditingFileManagementService {

    private static final String DEFAULT_FILE_FORMAT = ".png";
    private static final String FILE_PREFIX = "_";
    private final String SPLIT_DIR = "/split";

    private final Logger log = LoggerFactory.getLogger(EditingFileManagementService.class);

    @Autowired
    FileUtilService fileUtilService;

    @Autowired
    private ProjectService projectService;

    private final ApplicationProperties applicationProperties;

    private Path basePathEditionPath;

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

        String dir = constructPathFromEdition(edition);
        Files.createDirectories(Path.of(dir));
        Path pdfPath = Path.of(dir + Path.of(edition.getpDFFileName()));

        if(Files.exists(pdfPath)) {
            log.warn("The file already exists and is being replaced!");
            log.warn("Path: " + pdfPath);
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
        assert(edition.getpDFFileName() != null);
        assert(edition.getTitle() != null);

        String pdfPath = constructPathFromEdition(edition)
            + File.separator + edition.getpDFFileName();

        if(!edition.getpDFFileName().endsWith(".pdf")) {
           pdfPath += ".pdf";
        }

        PDDocument document = PDDocument.load(new File(pdfPath));

        return document;
    }

    public void extractPagesFromEdition(Edition e, JobDashboardProgressBar jobDashboardProgressBar) throws IOException {
        assert(e.getProjectId() != null);
        PDDocument loadedEditionPdf = this.loadPdfFile(e);

        Path imageStorageLocation = Path.of(constructPathFromEdition(e) + SPLIT_DIR);
        createOutputPathIfNotExistent(imageStorageLocation);

        fileUtilService.convertPdf2Img2(loadedEditionPdf, imageStorageLocation, startIndex, jobDashboardProgressBar);
    }

    /**
     * Returns true, if the page was already generated and a image with the correct page number is stored in the target
     * location
     * /split/_
     * @param e
     * @param pageNr
     * @return
     */
    public boolean isPageExistent(Edition e, int pageNr, String fileFormat) {
        return Files.exists(
            Path.of(constructPathFromEdition(e) + SPLIT_DIR + File.separator + FILE_PREFIX + pageNr + fileFormat)
        );
    }

    public boolean isPageExistent(Edition testEdition, int i) {
        return isPageExistent(testEdition, i , DEFAULT_FILE_FORMAT);
    }

    public BufferedImage loadPage(Edition e, int pageNr) throws PagesMightNotHaveBeenGeneratedException, IOException {
        if(!isPageExistent(e, pageNr)) {
            throw new PagesMightNotHaveBeenGeneratedException();
        }
        BufferedImage img = null;
        img = ImageIO.read(new File(constructPagePath(e, pageNr)));
        return img;
    }

    private void createOutputPathIfNotExistent(Path imageStorageLocation) throws IOException {
        if(!Files.exists(imageStorageLocation)) {
            Files.createDirectories(imageStorageLocation);
        }
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
        return basePathEditionPath.toString();
    }

    public void createNecessaryDirs () throws IOException {
        if(!Files.exists(basePathEditionPath)){
            Files.createDirectories(basePathEditionPath);
        }
    }

    public String constructPathFromEdition(Edition e) {
        return basePathEditionPath.toString() + File.separator + e.getProjectId() +
                File.separator + e.getId() + File.separator;
    }

    private String constructPathForSplittedPagesFromEdition(Edition e) {
        return constructPathFromEdition(e) + SPLIT_DIR;
    }

    public String constructPagePath(Edition e, int pageNr) {
        return constructPathFromEdition(e) + SPLIT_DIR + File.separator +FILE_PREFIX + pageNr + DEFAULT_FILE_FORMAT;
    }

    /**
     * If the splitting was already done, this method returns all the page Names.
     * @return
     */
    public List<String> getAllGeneratedScorePageFiles(Edition e) {
        return Arrays.stream(new File(
            constructPathForSplittedPagesFromEdition(e)
        )
            .listFiles())
            .filter(File::isFile)
            .map(File::getName)
            .collect(Collectors.toList());
    }

    public List<Long> getAllGeneratedScorePageFilesAsPageNr(Edition e) {
       return getAllGeneratedScorePageFiles(e).stream()
           .map(EditingFileManagementService::convertFileNameToPageNumber)
           .collect(Collectors.toList());
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public static Long convertFileNameToPageNumber(String p) {
        p = StringUtils.stripFilenameExtension(p);
        return Long.parseLong(
            p.replace("_","")
        );
    }

}
