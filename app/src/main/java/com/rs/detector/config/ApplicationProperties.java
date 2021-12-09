package com.rs.detector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties specific to Measure Detector.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

    private String editionResourceBasePath;

    private int imageSplitDPI;

    public MeasureDetector getMeasureDetector() {
        return measureDetector;
    }

    public void setMeasureDetector(MeasureDetector measureDetector) {
        this.measureDetector = measureDetector;
    }

    private MeasureDetector measureDetector;

    public String getEditionResourceBasePath() {
        return editionResourceBasePath;
    }

    public void setEditionResourceBasePath(String editionResourceBasePath) {
        this.editionResourceBasePath = editionResourceBasePath;
    }

    public int getImageSplitDPI() {
        return imageSplitDPI;
    }

    public void setImageSplitDPI(int imageSplitDPI) {
        this.imageSplitDPI = imageSplitDPI;
    }

    public static class MeasureDetector {

        private String host;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        private int port;
        private String endpoint;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }


        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

    }
}

