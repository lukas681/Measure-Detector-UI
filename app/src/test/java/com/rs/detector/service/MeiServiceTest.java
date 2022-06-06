package com.rs.detector.service;

import com.rs.detector.service.editing.SimpleDataInitialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeiServiceTestIT extends SimpleDataInitialization {

    @Autowired
    MeiService meiService;

    @BeforeEach
    void init() {
        super.setup();
    }

    @Test
    void generateMEIXML() throws IOException {
        meiService.generateMEIXML(testEdition);
    }

    @Test
    void loadTemplate() throws IOException {
        System.out.println(meiService.loadTemplate());
        assertNotNull(meiService.loadTemplate());
    }
}
