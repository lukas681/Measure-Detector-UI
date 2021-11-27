package com.rs.detector.service.experimental;

import com.rs.detector.web.api.ApiApiDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Service
//@Transactional
public class ExampleService implements ApiApiDelegate {

    @Override
    public Optional<NativeWebRequest> getRequest() {
        System.out.println("Delegated!!");
        return Optional.empty();
    }

    @Override
    public ResponseEntity<Void> apiListGet() {
        System.out.println("Delegated");
        return null;
    }
}
