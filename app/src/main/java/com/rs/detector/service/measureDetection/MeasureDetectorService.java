package com.rs.detector.service.measureDetection;

import java.awt.image.BufferedImage;

public interface MeasureDetectorService {


    /**
     *
     * @return
     */
    public MeasureDetectorResult process(BufferedImage pngImage);
}
