package com.rs.detector.service.measureDetection;

import com.rs.detector.web.api.model.ApiMeasureDetectorResult;

import java.awt.image.BufferedImage;

public interface MeasureDetectorService {


    /**
     *
     * @return
     */
    public ApiMeasureDetectorResult process(BufferedImage pngImage);
}
