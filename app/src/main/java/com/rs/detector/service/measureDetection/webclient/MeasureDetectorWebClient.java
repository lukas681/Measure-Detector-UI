package com.rs.detector.service.measureDetection.webclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import io.netty.handler.logging.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class MeasureDetectorWebClient {

    private final Logger log = LoggerFactory.getLogger(MeasureDetectorWebClient.class);

    private final WebClient client;

    public MeasureDetectorWebClient() {
        HttpClient httpClient =
            HttpClient.create()
                .wiretap(this.getClass().getCanonicalName(),
                    LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
            .compress(true);
        ReactorClientHttpConnector conn = new ReactorClientHttpConnector(httpClient.compress(true));

        this.client = WebClient
            .builder()
            .filters(exchangeFilterFunctions -> {
                exchangeFilterFunctions.add(logRequest());
            })
            .clientConnector(conn)
            .exchangeStrategies(getExchangeStrategies())
            .build();
    }

    public ApiMeasureDetectorResult detectMeasures(File img) {

        String query = ""; // Empty, but to mark that there WOULD be the option.
        String url = "localhost:8080/upload";

        var builder = new MultipartBodyBuilder();
//        builder.part("image", new FileSystemResource(img.getPath()))
//        .header("Content-Disposition", "form-data; name=image; filename=image.ong");

        var image = new FileSystemResource(img.getPath());
        builder.part("image", image)
            .header("Content-Disposition", "form-data; name=image; filename=image.ong");
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        var result = client.post()
            .uri(url)
            .header(HttpHeaders.CONTENT_LENGTH, "123") // This fucking header MUST be explicitly set .
            .contentType(MediaType.MULTIPART_FORM_DATA)
//            .body(BodyInserters.fromMultipartData("image", image))
            .bodyValue(multipartBody)
//            .headers(httpHeaders -> {
//                httpHeaders.add(HttpHeaders.ACCEPT, MediaType.MULTIPART_FORM_DATA);
//            })
            .exchange()
            .block()
            .bodyToMono(ApiMeasureDetectorResult.class)
            .block();
//            .bodyToMono(new ParameterizedTypeReference<List<ApiMeasureDetectorResult>>() {});
        return result;
    }

    private ExchangeStrategies getExchangeStrategies() {
        return ExchangeStrategies
            .builder()
            .codecs(clientDefaultCodecsConfigurer -> {
                clientDefaultCodecsConfigurer
                    .defaultCodecs()
                    .jackson2JsonEncoder(
                        new Jackson2JsonEncoder(
                            new ObjectMapper(),
                            MimeType.valueOf(MediaType.APPLICATION_JSON_VALUE)
                        )
                    );
                clientDefaultCodecsConfigurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
            })
            .build();
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder("Request: \n");
                clientRequest
                    .headers()
                    .forEach((name, values) -> values.forEach(value -> log.debug(value)));
                log.debug(sb.toString());
            }
            return Mono.just(clientRequest);
        });
    }

}