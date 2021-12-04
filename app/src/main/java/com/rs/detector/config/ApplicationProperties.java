package com.rs.detector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Measure Detector.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String editionResourceBasePath;

    public ApplicationProperties(String editionResourceBasePath) {
        this.editionResourceBasePath = editionResourceBasePath;
    }

    public String getEditionResourceBasePath() {
        return editionResourceBasePath;
    }

    public void setEditionResourceBasePath(String editionResourceBasePath) {
        this.editionResourceBasePath = editionResourceBasePath;
    }
}

