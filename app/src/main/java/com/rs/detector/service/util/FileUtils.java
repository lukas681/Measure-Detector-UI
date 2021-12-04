package com.rs.detector.service.util;

import com.rs.detector.security.DomainUserDetailsService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.h2.api.Interval;
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
public class FileUtils {

    private final Logger log = LoggerFactory.getLogger(FileUtils.class);

    // TODO REFACTOR
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
                    BufferedImage bim = pdfRenderer.renderImage(pageNumber);
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
     * @param firstPage first page on which to start converting
     * @throws IOException
     */
    public void convertPdf2Img2(@NotNull PDDocument documentToConvert, @NotNull Path outputPath, int firstPage) throws IOException {
        assert(documentToConvert != null);

        var pdfRenderer = new PDFRenderer(documentToConvert);
        String fileName = "";

        for (int pageNumber = firstPage; pageNumber < documentToConvert.getNumberOfPages(); ++pageNumber) {
            BufferedImage bim = pdfRenderer.renderImage(pageNumber);
            var destDir = outputPath.toString() + File.separator + fileName + "_" + pageNumber + ".png";
            log.debug("Destination: " + destDir);
            ImageIO.write(bim, "png", new File(destDir));
        }
        documentToConvert.close();
    }
}
