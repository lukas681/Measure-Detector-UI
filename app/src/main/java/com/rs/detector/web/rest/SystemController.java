package com.rs.detector.web.rest;

import com.rs.detector.web.api.SystemApiDelegate;
import com.rs.detector.web.api.model.ApiOrchJobStats;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SystemController implements SystemApiDelegate  {

    @Autowired
    StorageProvider storageProvider;

    private final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Override
    public ResponseEntity<ApiOrchJobStats> getJobStats() {
        var resRaw = storageProvider.getJobStats();
        ApiOrchJobStats res = new ApiOrchJobStats();
        try {
            res = new ApiOrchJobStats()
                .enqueued(resRaw.getEnqueued())
                .failed(resRaw.getFailed())
                .processing(resRaw.getProcessing())
                .scheduled(resRaw.getScheduled())
                .succeeded(resRaw.getSucceeded())
                .total(resRaw.getTotal());
        } catch(Exception ae) {
            return (ResponseEntity<ApiOrchJobStats>) ResponseEntity.badRequest();
        }
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(res);
    }
}
