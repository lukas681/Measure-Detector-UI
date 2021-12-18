package com.rs.detector.service.measureDetection.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rs.detector.config.ApplicationProperties;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import io.netty.handler.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.io.File;
import java.io.IOException;

@Service
@Profile("dev-mock")
public class MeasureDetectorWebClientMock implements MeasureDetectorWebClient {


    @Autowired
    ApplicationProperties applicationProperties;

    private final Logger log = LoggerFactory.getLogger(MeasureDetectorWebClientMock.class);

    public MeasureDetectorWebClientMock() {
    }

    //statically returning the same results. img explicitly allowed to be null
    @Override
    public ApiMeasureDetectorResult detectMeasures(File img) throws IOException {
        File f = new File("src/test/resources/scores/measure-detector-responses/_243.json");
        ObjectMapper o = new ObjectMapper();
        return o.readValue(f, ApiMeasureDetectorResult.class);
    }

}
