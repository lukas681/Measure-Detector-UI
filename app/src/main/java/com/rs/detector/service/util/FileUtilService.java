package com.rs.detector.service.util;

import com.rs.detector.config.ApplicationProperties;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public void mergePNGsToPDF() {
     /*   try (PDDocument doc = PDDocument.load(new File(inputFile)))
        {
            //we will add the image to the first page.
            PDPage page = doc.getPage(0);

            // createFromFile is the easiest way with an image file
            // if you already have the image in a BufferedImage,
            // call LosslessFactory.createFromImage() instead
            PDImageXObject pdImage = PDImageXObject.createFromFile(imagePath, doc);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true))
            {
                // contentStream.drawImage(ximage, 20, 20 );
                // better method inspired by http://stackoverflow.com/a/22318681/535646
                // reduce this value if the image is too large
                float scale = 1f;
                contentStream.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
            }
            doc.save(outputFile);
        } */
    }

    /**
     * Extracts all pages from a PDF document and stores it to a defined location.
     * @param documentToConvert the document to convert
     * @param outputPath The location where the pngs should be stores
     * @param firstPage first page on which to start converting. Index Starting at zero.
     * @throws IOException
     */
    public void convertPdf2Img2(@NotNull PDDocument documentToConvert, @NotNull Path outputPath, int firstPage,
                                JobContext jobContext) throws IOException {
        JobDashboardProgressBar jobDashboardProgressBar = null;
        if(jobContext != null) {
            jobDashboardProgressBar = jobContext.progressBar(100);
        }

        assert(documentToConvert != null);
        assert(firstPage >= 0 && firstPage <= documentToConvert.getNumberOfPages()); // Capture DIVBYZERO

        var pdfRenderer = new PDFRenderer(documentToConvert);
        int pagesToProcess = documentToConvert.getNumberOfPages() - firstPage + 1;

        for (int pageNumber = firstPage; pageNumber < documentToConvert.getNumberOfPages(); ++pageNumber) {
            if(jobContext != null) {
                jobContext.logger().info("Processing Page " + (pageNumber + 1) + " out of " + documentToConvert.getNumberOfPages());
            }

            BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNumber, imageSplitDPI, ImageType.RGB);
            var destDir = outputPath.toString() + File.separator + "_" + pageNumber + ".png";
            log.debug("Destination: " + destDir);
            ImageIO.write(bim, "png", new File(destDir));
            updateProgress(jobDashboardProgressBar, pagesToProcess, pageNumber);
        }
        documentToConvert.close();
    }

    private void updateProgress(JobDashboardProgressBar jobDashboardProgressBar, int pagesToProcess, int pageNumber) {
        if(jobDashboardProgressBar!= null) {
            jobDashboardProgressBar.setValue(((pageNumber / pagesToProcess) * 100));
        }
    }

    /**
     * Extracts all pages from a PDF document and stores it to a defined location.
     * @param documentToConvert the document to convert
     * @param outputPath The location where the pngs should be stores
     * @throws IOException
     */
    public void convertPdf2Img2(@NotNull PDDocument documentToConvert, @NotNull Path outputPath,
                                JobContext jobContext) throws IOException {
        this.convertPdf2Img2(documentToConvert, outputPath, 0, jobContext);
    }

    public InputStream getPDF(String pdfPath) throws FileNotFoundException {
        try {
            return new FileInputStream(pdfPath);
        } catch (IOException e) {
            log.error("An error has occured while loading the PDF file",e.getMessage());
            throw e;
        }
    }
}
