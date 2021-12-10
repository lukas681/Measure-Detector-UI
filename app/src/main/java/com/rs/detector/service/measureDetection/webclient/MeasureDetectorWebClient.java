package com.rs.detector.service.measureDetection.webclient;

import com.rs.detector.web.api.model.ApiMeasureDetectorResult;

import java.io.File;
import java.io.IOException;

public interface MeasureDetectorWebClient {

    ApiMeasureDetectorResult detectMeasures(File img) throws IOException;
}
