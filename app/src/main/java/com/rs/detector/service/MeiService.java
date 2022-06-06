package com.rs.detector.service;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.MeasureBox;
import com.rs.detector.service.editing.EditingFileManagementService;
import com.rs.detector.service.editing.EditingService;
import com.rs.detector.service.editing.ScorePageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;

@Service
public class MeiService {

    private final Logger log = LoggerFactory.getLogger(EditingService.class);

    @Autowired
    EditingService editingService;

    @Autowired
    PageService pageService;

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    MeasureBoxService measureBoxService;


    public String generateMEIXML(Edition edition) throws IOException {

        var res = generateSurfacesAndMeasures(edition);
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

        for(var pn: editingFileManagementService.getAllGeneratedScorePageFilesAsPageNr(e)) {
            var zone = "";
            var surface = String.format("<graphic xml:id=\"%s\" target=\"%s\"/>",
            null, // name
            null    ); // target

            var p = editingService.searchPageInRepository(e, Long.valueOf(pn));
            if(p.isPresent()) {
                var measureBoxes =   measureBoxService.findAllByPageId(p.get().getId())
                    .collectList()
                    .toProcessor()
                    .block();
                addMeasureBoxes(surface, zone, measureBoxes);
                res.put("surfaces", res.getOrDefault("surfaces", "")
                    + String.format("<surface xml:id=\"%s\" n=\"%s\">%s</surface>", // Maybe we need the dimension as well?
                    null, // surface_id
                    null, // pagenr
                    null)); // content
                res.put("measures", res.getOrDefault("measures", "")
                    + zone);
            }
        }
        return res;
    }

    private void addMeasureBoxes(String page, String zone, List<MeasureBox> measureBoxes) {
        for(var m: measureBoxes) {
            page = String.format("%s\n<zone xml:id=\"%s\" type=\"measure\" ulx=\"%s\" uly=\"%s\" lrx=\"%s\" lry=\"%s\"/>",
                page,
                null, // zoneid
                null, // ulx
                null, // uly
                null, // lrx
                null // lry
            );

            zone = String.format("%s<measure xml:id=\"%s\" n=\"%s\" label=\"%s\" facs=\"#%s\"/>",
                zone,
                null, // measureid
                null, // measure nr
                null, // label
                null // zone id
            );
        }
    }

}
