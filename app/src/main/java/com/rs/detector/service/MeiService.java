package com.rs.detector.service;

import com.rs.detector.domain.Edition;
import com.rs.detector.service.editing.EditingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;

@Service
public class MeiService {

    private final Logger log = LoggerFactory.getLogger(EditingService.class);

    public String generateMEIXML(Edition edition) throws IOException {


        var res = generateSurfacesAndMeasures();
        var mei = loadTemplate()
            .replace("${SURFACES}", res.get("surfaces"))
            .replace("${MEASURES}", res.get("measures"))
            .replace("${ISODATE}", LocalDateTime.now().toString());;


        return mei;

    }
    public String loadTemplate() throws IOException {

        var inStr = new ClassPathResource("templates/mei/raw.mei").getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(inStr, UTF_8));
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = in.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
        }
        return content.toString();
    }
    public Map<String, String> generateSurfacesAndMeasures(Edition e) {
        HashMap res = new HashMap<String, String>();
        e.get

    }


}
