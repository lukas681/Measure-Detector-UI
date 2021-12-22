package com.rs.detector.web.rest;

import com.rs.detector.web.api.EditionApi;
import com.rs.detector.web.api.EditionApiDelegate;
import com.rs.detector.web.api.model.ApiOrchEditionWithFile;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Service
public class EditionController implements EditionApiDelegate {

    @Override
    public ResponseEntity<Void> addEdition(@ApiParam(value = "", required = true) @Valid @RequestBody ApiOrchEditionWithFile apiOrchEditionWithFile) {
        System.out.println(apiOrchEditionWithFile.getPdfFile().getFilename());
        return null;
    }

}
