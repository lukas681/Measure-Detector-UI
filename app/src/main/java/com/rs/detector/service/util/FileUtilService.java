package com.rs.detector.service.util;

import com.rs.detector.config.ApplicationProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class FileUtilService {

    private final Logger log = LoggerFactory.getLogger(FileUtilService.class);

    final ApplicationProperties applicationProperties;
    private int imageSplitDPI;

    public FileUtilService(ApplicationProperties applicationProperties)  {
        this.applicationProperties = applicationProperties;
        imageSplitDPI = applicationProperties.getImageSplitDPI();
    }

    public String convertPdf2Img(String fileInput, Path path, int firstPage) {
        var destDir = "";
        try {
            var destinationDir = path.toString();
            var sourceFile = new File(fileInput);
            var destinationFile = new File(destinationDir);

            if (!destinationFile.exists()) {
                destinationFile.mkdir();
                log.debug("Folder Created -> " + destinationFile.getAbsolutePath());
            }

            if (sourceFile.exists()) {
                var document = PDDocument.load(sourceFile);
                var pdfRenderer = new PDFRenderer(document);

                String fileName = sourceFile.getName().replace(".pdf", "");

                for (int pageNumber = firstPage; pageNumber < document.getNumberOfPages(); ++pageNumber) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, imageSplitDPI);
                    destDir = destinationDir + File.separator + fileName + "_" + pageNumber + ".png";
                    log.debug("Destination Directory: " + destinationDir);
                    ImageIO.write(bim, "png", new File(destDir));
                }
                document.close();
                log.error(sourceFile.getName() + " File does not exist");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return destDir;
    }

    /**
     * Extracts all pages from a PDF document and stores it to a defined location.
     * @param documentToConvert the document to convert
     * @param outputPath The location where the pngs should be stores
     * @param firstPage first page on which to start converting. Index Starting at zero.
     * @param jobDashboardProgressBar
     * @throws IOException
     */
    public void convertPdf2Img2(@NotNull PDDocument documentToConvert, @NotNull Path outputPath, int firstPage, JobDashboardProgressBar jobDashboardProgressBar) throws IOException {
        assert(documentToConvert != null);

        var pdfRenderer = new PDFRenderer(documentToConvert);
        String fileName = "";

        // TODO Implement status update
        for (int pageNumber = firstPage; pageNumber < documentToConvert.getNumberOfPages(); ++pageNumber) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, imageSplitDPI, ImageType.RGB);
            var destDir = outputPath.toString() + File.separator + fileName + "_" + pageNumber + ".png";
            log.debug("Destination: " + destDir);
            ImageIO.write(bim, "png", new File(destDir));
            updateProgress(jobDashboardProgressBar);
        }
        documentToConvert.close();
    }

    private void updateProgress(JobDashboardProgressBar jobDashboardProgressBar) {
    }

    /**
     * Extracts all pages from a PDF document and stores it to a defined location.
     * @param documentToConvert the document to convert
     * @param outputPath The location where the pngs should be stores
     * @throws IOException
     */
    public void convertPdf2Img2(@NotNull PDDocument documentToConvert, @NotNull Path outputPath,
                                JobDashboardProgressBar jobDashboardProgressBar) throws IOException {
        this.convertPdf2Img2(documentToConvert, outputPath, 0, jobDashboardProgressBar);
    }
}
