package com.rs.detector.service.measureDetection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MeasureDetectorServiceTest {

    @Autowired
    MeasureDetectorService measureDetectorService;

    @Test
    void test1() {
        measureDetectorService.test();
    }
}
