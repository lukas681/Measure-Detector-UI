package com.rs.detector.service.editing;

import com.rs.detector.config.ApplicationProperties;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.service.ProjectService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.service.util.FileUtilService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    ResourceLoader resourceLoader;

    @Autowired
    FileUtilService fileUtilService;

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

    // Tag: split
    public void extractPagesFromEdition(Edition e, JobContext jobContext) throws IOException {
        log("Starting to create png images of the  pages out of given pdf!", jobContext);
        assert(e.getProjectId() != null);
        PDDocument loadedEditionPdf = this.loadPdfFile(e);

        Path imageStorageLocation = Path.of(constructPathFromEdition(e) + SPLIT_DIR);
            log("Output location: " + basePathEditionPath.toAbsolutePath(), jobContext);
        createOutputPathIfNotExistent(imageStorageLocation);

        fileUtilService.convertPdf2Img2(loadedEditionPdf, imageStorageLocation, startIndex, jobContext);
    }

    public void log(String s, JobContext jobContext) {
        if(jobContext != null) {
            jobContext.logger().info(s);
        }
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

    public Resource getPage(Edition e, int pageNr) throws FileNotFoundException, MalformedURLException {
       if(isPageExistent(e, pageNr)) {
           return new UrlResource("file", constructPagePath(e, pageNr));
       }
       return null;
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
     * Delete all generated files for this edition.
     * @param e
     */
    public void deleteEditionFiles(Edition e) throws IOException {
        Path basePathEdition = Path.of(constructPathFromEdition(e));
        log.debug("Deleting all generated files for this edition: " + e.getTitle() + "on the base path " + basePathEdition.toString());
        traverseDelete(basePathEdition);
    }

    /**
     * rm -rf project
     * @param p
     * @throws IOException
     */
    public void deleteProjectFiles(Project p) throws IOException {
        Path basePathEdition = Path.of(getBasePathEdition() + File.separator + p.getId());
        log.debug("Deleting all generated files for this project: " + p);
        traverseDelete(basePathEdition);
    }

    /**
     * Corresponds to rm -rf edition
     * @param p
     */
    private void traverseDelete(Path p) throws IOException {
        if(Files.exists(p)) {
            Files.walk(p)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
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

    public String constructPathForSplittedPagesFromEdition(Edition e) {
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
            constructPathForSplittedPagesFromEdition(e))
            .listFiles())
            .filter(File::isFile)
            .map(File::getName)
            .collect(Collectors.toList());
    }

    public List<Integer> getAllGeneratedScorePageFilesAsPageNr(Edition e) {
       return getAllGeneratedScorePageFiles(e).stream()
           .map(EditingFileManagementService::convertFileNameToPageNumber)
           .sorted()
           .collect(Collectors.toList());
    }

    /**
     * This method constructs a resulting pdf from the pngs annotated with the measures
     */
    public void createPdfWithMeasures(Edition e) throws IOException, PagesMightNotHaveBeenGeneratedException {
        for(var pn: getAllGeneratedScorePageFilesAsPageNr(e)) {
            var image = loadPage(e, pn);


        }

        // get all Files in directory
        var x =  this.getPage(e, 0);

    }

    public static void combineImagesIntoPDF(String pdfPath, String... inputDirsAndFiles) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            for (String input : inputDirsAndFiles) {
                Files.find(Paths.get(input),
                        Integer.MAX_VALUE,
                        (path, basicFileAttributes) -> Files.isRegularFile(path))
                    .forEachOrdered(path -> addImageAsNewPage(doc, path.toString()));
            }
            doc.save(pdfPath);
        }
    }

    // TODO Mayber add higher resolution
    private static void addImageAsNewPage(PDDocument doc, String imagePath) {
        try {
            PDImageXObject image          = PDImageXObject.createFromFile(imagePath, doc);
            PDRectangle pageSize       = PDRectangle.A4;

            int            originalWidth  = image.getWidth();
            int            originalHeight = image.getHeight();
            float          pageWidth      = pageSize.getWidth();
            float          pageHeight     = pageSize.getHeight();
            float          ratio          = Math.min(pageWidth / originalWidth, pageHeight / originalHeight);
            float          scaledWidth    = originalWidth  * ratio;
            float          scaledHeight   = originalHeight * ratio;
            float          x              = (pageWidth  - scaledWidth ) / 2;
            float          y              = (pageHeight - scaledHeight) / 2;

            PDPage page           = new PDPage(pageSize);
            doc.addPage(page);
            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.drawImage(image, x, y, scaledWidth, scaledHeight);
            }
            System.out.println("Added: " + imagePath);
        } catch (IOException e) {
            System.err.println("Failed to process: " + imagePath);
            e.printStackTrace(System.err);
        }
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public static Integer convertFileNameToPageNumber(String p) {
        p = StringUtils.stripFilenameExtension(p);
        return Integer.parseInt(
            p.replace("_","")
        );
    }

    public Path getBasePathEditionPath() {
        return basePathEditionPath;
    }

}
