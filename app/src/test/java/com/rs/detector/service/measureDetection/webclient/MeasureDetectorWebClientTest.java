package com.rs.detector.service.measureDetection.webclient;

import com.rs.detector.IntegrationTest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeasureDetectorWebClientTest {

    @Autowired
    MeasureDetectorWebClient measureDetectorWebClient;

    @Test
    void detectMeasures() {
        File f = new File("src/test/resources/scores/split/_243.png");
        var res = (measureDetectorWebClient.detectMeasures(f));
        System.out.println(res);

    }
}
