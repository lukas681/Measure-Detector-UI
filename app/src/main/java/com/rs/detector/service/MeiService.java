package com.rs.detector.service;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.MeasureBox;
import com.rs.detector.domain.Page;
import com.rs.detector.service.editing.EditingFileManagementService;
import com.rs.detector.service.editing.EditingService;
import com.rs.detector.service.editing.ScorePageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.*;

@Service
public class MeiService {

    private final Logger log = LoggerFactory.getLogger(EditingService.class);

    @Autowired
    @Lazy
    EditingService editingService;

    @Autowired
    PageService pageService;

    @Autowired
    EditingFileManagementService editingFileManagementService;

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    MeasureBoxService measureBoxService;


    /**
     * Main Method for generating the MEI File
     * @param edition
     * @return
     * @throws IOException
     */
    public String generateMEIXML(Edition edition) throws IOException {
        var res = generateSurfacesAndMeasures(edition);
        var mei = loadTemplate()
            .replace("${SURFACES}", res.get("surfaces"))
            .replace("${MEASURES}", res.get("measures"))
            .replace("${ISODATE}", LocalDateTime.now().toString())
            .replace("${MDIVID}", "mdiv_" + getAlphaNumericString(26)
            );
        System.out.printf(mei);
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
        res.put("surfaces", "");
        res.put("measures","");

        for(var pn: Objects.requireNonNull(pageService.findAllByEdition(e.getId())
            .collectList()
            .block())
        ) {
            var zones = String.format("\n<graphic xml:id=\"%s\" target=\"%s\"/>",
            "graphic_" + getAlphaNumericString(26), // name
            "out.pdf"); // target
            var measures = "";

            var p = editingService.searchPageInRepository(e, pn.getPageNr());
            if(p.isPresent()) {
                var measureBoxes =   measureBoxService.findAllByPageId(p.get().getId())
                    .collectList()
                    .toProcessor()
                    .block();
                var mz = addMeasureBoxes(measures, zones, measureBoxes, p.get());
                measures = mz[1];
                zones = mz[0];

                res.put("surfaces", res.getOrDefault("surfaces", "")
                    + String.format("<surface xml:id=\"%s\" n=\"%s\">%s\n</surface>", // Maybe we need the dimension as well?
                    "surface_" + getAlphaNumericString(26), // surface_id
                    pn.getPageNr(),
                    zones // pageNr
                    )); // content
                res.put("measures", res.getOrDefault("measures", "")
                    + measures);
            }
        }
        return res;
    }

    private String[] addMeasureBoxes(String measure, String zone, List<MeasureBox> measureBoxes, Page pn) {
        for(var m: measureBoxes) {
            String zoneID = "zone_" + getAlphaNumericString(26);
            zone = String.format("%s\n<zone xml:id=\"%s\" type=\"measure\" ulx=\"%s\" uly=\"%s\" lrx=\"%s\" lry=\"%s\"/>",
                zone,
                zoneID, // zoneid
                m.getUlx(), // ulx
                m.getUly(), // uly
                m.getLrx(), // lrx
                m.getLry() // lry
            );

            measure = String.format("%s\n<measure xml:id=\"%s\" n=\"%s\" label=\"%s\" facs=\"#%s\"/>",
                measure,
                "measure_" + getAlphaNumericString(26), // measureid
                m.getMeasureCount() + pn.getMeasureNumberOffset(), // measure nr // Just the offset ... TODO Add count function
                m.getMeasureCount() + pn.getMeasureNumberOffset(), // label
                zoneID // zone id
            );
        }
        return new String[] {
            zone, measure
        };
    }

    // function to generate a random string of length n
    static String getAlphaNumericString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index
                = (int)(AlphaNumericString.length()
                * Math.random());
            sb.append(AlphaNumericString
                .charAt(index));
        }
        return sb.toString();
    }

}
