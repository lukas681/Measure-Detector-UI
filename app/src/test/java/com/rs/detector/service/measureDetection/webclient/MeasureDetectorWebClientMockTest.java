package com.rs.detector.service.measureDetection.webclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * This test is independent of the set profile and testrun.
 * Sets the Profile to dev-mock and tests whether the correct WebClient is mocked.
 */
@SpringBootTest
@ActiveProfiles("dev-mock")
class MeasureDetectorWebClientMockTestIT {

    @Autowired
    MeasureDetectorWebClient measureDetectorWebClient;

    @Test
    void detectMeasures() {
        System.out.println("Profile Dev Mock Correctly. Testing correct autowiring");
        assert(measureDetectorWebClient.getClass().equals(MeasureDetectorWebClientMock.class));

        assert(!measureDetectorWebClient.getClass().equals(MeasureDetectorWebClientProd.class));
    }
}

