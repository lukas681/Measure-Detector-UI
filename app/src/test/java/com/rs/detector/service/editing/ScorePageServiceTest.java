package com.rs.detector.service.editing;

import com.rs.detector.domain.MeasureBox;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.MeasureBoxRepository;
import com.rs.detector.service.PageService;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import com.rs.detector.service.measureDetection.MeasureDetectorService;
import com.rs.detector.web.api.model.ApiMeasureDetectorResult;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "application.editionResourceBasePath=build/res",
    "application.imageSplitDPI=400"
})
class ScorePageServiceTest extends SimpleDataInitialization {

    JobContext jobContext= JobContext.Null;

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    PageService pageService;

    @Autowired
    MeasureDetectorService measureDetectorService;

    @Autowired
    MeasureBoxRepository measureBoxRepository;

    @Autowired
    EditingService editingService;

    @BeforeEach
    void init() {
        super.setup();
    }

    @Value("${spring.profiles.active}")
    private String activeProfiles;

    @Test
    void generatePageObjectIfNotExistent() throws PagesMightNotHaveBeenGeneratedException, IOException {
        var pdf = PDDocument
            .load(new File("src/test/resources/scores/aegyptische-helena.pdf")
            );

        // We do not want to generate everything of the 248 example pages ... This is something for the user only :)
        editingService.getEditingFileManagementService().setStartIndex(243);

        // This already handles the database storage
        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition, jobContext);

        scorePageService.generatePageObjectIfNotExistent(testEdition);

        StepVerifier
            .create(scorePageService.generatePageObjectIfNotExistent(testEdition))
            // Page 243 should not have been generated so far.
            .expectNextMatches((m->m.getPageNr() == 244l))
            .then( () -> System.out.println("Test"))
            .expectComplete()
            .verify();

        var res=  pageRepository.findAllByEditionId(testEdition.getId())
            .collect(Collectors.toList())
            .block();
        res.forEach(System.out::println);
        assert(res.size() == 5);

        scorePageService.generatePageObjectIfNotExistent(testEdition).collect(Collectors.toList()).block();
        res=  pageRepository.findAllByEditionId(testEdition.getId())
            .collect(Collectors.toList())
            .block();
        res.forEach(System.out::println);
        assert(res.size() == 5); // Nothing should have changed.
    }

    @Test
    void sortAndFillNextLinks() {
        var pageList = new ArrayList<Page>();

        for(Long i = 20l; i > 0; i--) {
            pageList.add(new Page().pageNr(i));
        }

        scorePageService.sortAndFillNextLinks(pageList);
        for(int i = 0; i < 10; i++) {
            assert(pageList.get(i).getNextPage().equals(pageList.get(i+1).getPageNr()));
        }

    }

    @Test
    void findPageByEditionIdAndPageNr() throws PagesMightNotHaveBeenGeneratedException, IOException {
        var pdf = PDDocument
            .load(new File("src/test/resources/scores/aegyptische-helena.pdf")
            );
        editingService.getEditingFileManagementService().setStartIndex(243);
        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition, jobContext);
        scorePageService.generatePageObjectIfNotExistent(testEdition).blockLast();

        var res = scorePageService.findPageByEditionIdAndPageNr(testEdition, 244l);
        assertNotNull(res);
    }

    @Test
    void valueOfCutDecimals() {
        String s1 = "1234.1234567856";
        String s2 = "0.00000002";
        String s3 = "123.123123123123123123";
        String s4 = "123455";
        String s5 = "123.123123123123123123123123123";

        assert(ScorePageService.valueOfCutDecimals(s1)==1234);
        assert(ScorePageService.valueOfCutDecimals(s2)==0);
        assert(ScorePageService.valueOfCutDecimals(s3)==123);
        assert(ScorePageService.valueOfCutDecimals(s4)==123455);
        assert(ScorePageService.valueOfCutDecimals(s5)==123);
    }

    @Test
    void addMeasureDetectorResultBoxesToPage() throws IOException {
        System.out.println("Active Profile:" + activeProfiles);
        var res =  measureDetectorService.process(null);
        scorePageService.addMeasureDetectorResultBoxesToPage(res, testEdition, 245l).blockLast();
//        var dbRes = measureBoxRepository.findAll().collect(Collectors.toList()).block();
        var dbRes = measureBoxRepository
            .findByPage(testPage.getId())
            .collect(Collectors.toList()).block();

        dbRes.forEach(System.out::println);
        assert(dbRes.size()==8);
        System.out.println(res);
    }

    @Test
    void deleteAllMeasureBoxes() throws IOException, InterruptedException {
        System.out.println("Active Profile:" + activeProfiles);
        var res =  measureDetectorService.process(null);
        scorePageService.addMeasureDetectorResultBoxesToPage(res, testEdition, 245l).blockLast();
        var dbRes = measureBoxRepository
            .findByPage(testPage.getId())
            .collect(Collectors.toList()).block();
        assert(dbRes.size()==8);

        // TODO NON Reactive with Step Verifier, but this might be more complicated as we have to do multiple calls
        pageService.deleteAllMeasureBoxes(testPage.getId()).blockLast();

        dbRes = measureBoxRepository
            .findByPage(testPage.getId())
            .collect(Collectors.toList()).block();
        assert(dbRes.size() == 0); //Asuming prepulated by super.setup
    }

    @Test
    void updatePageMeasureBoxesWithMDResult() throws IOException {
        List<com.rs.detector.web.api.model.MeasureBox> newMeasureBoxes = new ArrayList() {
            {
                add(new com.rs.detector.web.api.model.MeasureBox().left("123").right("235").top("567").bottom("765"));
                add(new com.rs.detector.web.api.model.MeasureBox().left("126").right("239").top("576").bottom("770"));
                add(new com.rs.detector.web.api.model.MeasureBox().left("128").right("235").top("667").bottom("795"));
                add(new com.rs.detector.web.api.model.MeasureBox().left("129").right("235").top("767").bottom("805"));
            }
        };
        var newMeasureDetectorResult = new ApiMeasureDetectorResult()
            .measures(newMeasureBoxes);

        var res =  measureDetectorService.process(null);
        scorePageService.addMeasureDetectorResultBoxesToPage(res, testEdition, 245l).blockLast();
        var debug = scorePageService.updatePageMeasureBoxesWithMDResult(testPage, newMeasureDetectorResult).blockLast();

        var dbRes = measureBoxRepository
            .findByPage(testPage.getId())
            .collect(Collectors.toList()).block();

        assert(dbRes.size() == 4); //Asuming prepulated by super.setup
    }
    @Test
    void drawRectangleInPDF() throws PagesMightNotHaveBeenGeneratedException, IOException {
        File f = new File("src/test/resources/scores/aegyptische-helena.pdf");
        PDDocument pdf = PDDocument
            .load(f);
        PDPage p1 = pdf.getPage(0);
        PDPageContentStream contentStream = new PDPageContentStream(pdf, p1);
        contentStream.setStrokingColor(Color.DARK_GRAY);
        contentStream.addRect(200, 200, 100, 100);
        contentStream.fill();
        contentStream.close();

        f = new File("src/test/resources/scores/aegyptische-helena-res.pdf");
        pdf.save(f);
    }

    @Test
    void addRectangleToPNG() throws PagesMightNotHaveBeenGeneratedException, IOException {

        var f = new File("src/test/resources/scores/split/_243.png");
        BufferedImage img = ImageIO.read(f);

        // Adding Rectangle
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.RED);
//        g2d.drawRect(0, 0, 1000, 1000);
        g2d.setComposite( AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.075f));
        g2d.fillRect(0 , 0, 1000, 1000);

       // Adding Measure Number
        int fontSize = img.getHeight() / 30;
         g2d = img.createGraphics();
         g2d.setColor(Color.BLACK);
         g2d.setFont(new Font("Purisa", Font.PLAIN, fontSize));
        g2d.setComposite( AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, 0.7f));
        g2d.drawString("This is a test", 0, fontSize + 1);

        ImageIO.write(img, "png", new File("src/test/resources/scores/split/_243-1.png"));

    }
}
