package com.rs.detector.service.measureDetection;

import com.rs.detector.web.api.model.ApiMeasureDetectorResult;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public interface MeasureDetectorService {

    /**
     *
     * @return
     */
    public ApiMeasureDetectorResult process(File pngImage) throws IOException;
}
