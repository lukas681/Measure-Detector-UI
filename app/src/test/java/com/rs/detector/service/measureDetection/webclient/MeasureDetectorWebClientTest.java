package com.rs.detector.service.measureDetection.webclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rs.detector.IntegrationTest;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
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
    void detectMeasuresAndTestConnectionToMeas() {
        File f = new File("src/test/resources/scores/split/_243.png");
        var res = (measureDetectorWebClient.detectMeasures(f));
        assert(res != null);
        System.out.println(res);
    }
    @Test
    void testCorrectJSONParsing() throws JsonProcessingException {
       String example= "{\\\"measures\\\": [{\\\"left\\\": 356.19469195604324, \\\"bottom\\\": 3745.3090223670006, " +
           "\\\"top\\\": 299.4914470613003, \\\"right\\\": 1035.8701753616333}, {\\\"left\\\": 983.9735279083252, " +
           "\\\"bottom\\\": 3718.7108721733093, \\\"top\\\": 271.07974978908896, \\\"right\\\": 1587.2781744003296}, " +
           "{\\\"left\\\": 1540.9383482933044, \\\"bottom\\\": 3673.9094014167786, \\\"top\\\": 301.89786115288734, " +
           "\\\"right\\\": 2029.1941452026367}, {\\\"left\\\": 1999.1079487800598, \\\"bottom\\\": 2962" +
           ".0880155563354, \\\"top\\\": 370.2402871772647, \\\"right\\\": 2365.171365261078}, {\\\"left\\\": 2332" +
           ".980797290802, \\\"bottom\\\": 2937.149121463299, \\\"top\\\": 365.5736293941736, \\\"right\\\": 2695" +
           ".2541880607605}, {\\\"left\\\": 1556.356939792633, \\\"bottom\\\": 3617.3725449442863, \\\"top\\\": 2966.8951382637024, \\\"right\\\": 2043.9851741790771}, {\\\"left\\\": 1998.7958793640137, \\\"bottom\\\": 3633.5418633818626, \\\"top\\\": 2989.476114809513, \\\"right\\\": 2375.6007833480835}, {\\\"left\\\": 2335.1832575798035, \\\"bottom\\\": 3637.147205412388, \\\"top\\\": 2969.484905600548, \\\"right\\\": 2705.2417788505554}]}\\r\\n";
        ObjectMapper o = new ObjectMapper();
        var res = o.readValue(example, ApiMeasureDetectorResult.class);
        assert(res.getMeasures().size()==8);
    }
}
