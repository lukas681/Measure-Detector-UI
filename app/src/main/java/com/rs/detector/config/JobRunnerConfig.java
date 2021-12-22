package com.rs.detector.config;

import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunnerConfig {

        // The`spring-boot-starter-web` provides Jackson as JobMapper
        @Bean
        public StorageProvider storageProvider(JobMapper jobMapper) {
            InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
            storageProvider.setJobMapper(jobMapper);
            return storageProvider;
        }
}
