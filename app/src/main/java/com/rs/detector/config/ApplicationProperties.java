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

    public String getEditionResourceBasePath() {
        return editionResourceBasePath;
    }

    public void setEditionResourceBasePath(String editionResourceBasePath) {
        this.editionResourceBasePath = editionResourceBasePath;
    }
}

