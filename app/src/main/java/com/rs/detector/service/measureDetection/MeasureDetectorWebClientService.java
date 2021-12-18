package com.rs.detector.service.measureDetection;

import com.rs.detector.service.measureDetection.webclient.MeasureDetectorWebClient;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class MeasureDetectorWebClientService implements MeasureDetectorService {

    @Autowired
    MeasureDetectorWebClient measureDetectorWebClient;

    @Override
    public ApiMeasureDetectorResult process(File pngImage) throws IOException {
        return measureDetectorWebClient.detectMeasures(pngImage);
    }

}
