package com.rs.detector.service.editing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScorePageServiceTest extends SimpleDataInitialization {

    @BeforeEach
    void init() {
        super.setup();

    }

    @Test
    void generatePageObjectIfNotExistent() {
        System.out.println("Test");
    }
}
