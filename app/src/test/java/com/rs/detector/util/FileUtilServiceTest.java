package com.rs.detector.util;

import com.rs.detector.service.editing.EditingFileManagementService;
import com.rs.detector.service.util.FileUtilService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class FileUtilServiceTest {

    // "application.editionResourceBasePath=build/res",
    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    FileUtilService utils;

    @Test
    public void testconvert2PDF() throws URISyntaxException {

        System.out.println(editingFileManagementService);
        URL resourceUrl = getClass().getResource("/scores/aegyptische-helena.pdf");

        File dir = new File("build/res/scores/out");
        File dir2 = new File("build/res/scores/");
        boolean isCreated = dir2.mkdir();
        System.out.println(isCreated);
        isCreated = dir.mkdir();
        System.out.println(isCreated);

        System.out.println(dir.getAbsolutePath());

        utils.convertPdf2Img(resourceUrl.getPath(),
            Path.of(dir.getAbsolutePath()), 245);

    }
}
