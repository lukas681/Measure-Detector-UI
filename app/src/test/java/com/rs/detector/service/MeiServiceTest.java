package com.rs.detector.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeiServiceTest {

    @Autowired
    MeiService meiService;


    @Test
    void generateMEIXML() throws IOException {
        meiService.generateMEIXML(null);

    }

    @Test
    void loadTemplate() throws IOException {
        System.out.println(meiService.loadTemplate());
        assertNotNull(meiService.loadTemplate());
    }
}
