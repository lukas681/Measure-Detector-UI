package com.rs.detector.util;

import com.rs.detector.service.util.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@ActiveProfiles("test")
@SpringBootTest(classes = FileUtilsTest.class)
public class FileUtilsTest {

    @Autowired
    FileUtils utils;

    @Test
    public void testconvert2PDF() throws URISyntaxException {
        URL resourceUrl = getClass().getResource("/scores/aegyptische-helena.pdf");
        File dir = new File("/scores/out");
        boolean isCreated = dir.mkdir();

        utils.convertPdf2Img(resourceUrl.getPath(), Paths.get(getClass().getResource( "/scores/out").toURI()), 199);

    }
}
