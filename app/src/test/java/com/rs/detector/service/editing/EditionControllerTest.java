package com.rs.detector.service.editing;

import com.rs.detector.web.api.model.ApiOrchEditionWithFileApiOrchEditionWithFileAsString;
import com.rs.detector.web.api.model.ApiOrchEditionWithFileAsString;
import com.rs.detector.web.rest.EditionController;
import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.*;


@SpringBootTest
public class EditionControllerTest {

    @Autowired
    EditionController editionController;

    @Test
    // TODO Might give problems with large filesizes
    public void testEditionUpload() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/resources/other/base64pdf.txt");
        String stringTooLong = IOUtils.toString(fis);
        var testEdition = new ApiOrchEditionWithFileAsString()
            .title("12312312")
            .createdDate("2021-12-22T10:53:00.000Z")
            .description("123")
            .pdfFile("")
            .type("SCORE")
            .pDFFileName(stringTooLong);
        var res = editionController.parseDataFromBase64Encoded(stringTooLong);

        FileUtils.writeByteArrayToFile(new File("build/res/out.pdf"), res);
    }
}
