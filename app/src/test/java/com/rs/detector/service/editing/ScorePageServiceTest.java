package com.rs.detector.service.editing;

import com.rs.detector.domain.Page;
import com.rs.detector.service.editing.exceptions.PagesMightNotHaveBeenGeneratedException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "application.editionResourceBasePath=build/res",
    "application.imageSplitDPI=400"
})
class ScorePageServiceTest extends SimpleDataInitialization {

    @Autowired
    ScorePageService scorePageService;

    @Autowired
    EditingService editingService;

    @BeforeEach
    void init() {
        super.setup();

    }

    @Test
    void generatePageObjectIfNotExistent() throws PagesMightNotHaveBeenGeneratedException, IOException {
        var pdf = PDDocument
            .load(new File("src/test/resources/scores/aegyptische-helena.pdf")
            );

        // We do not want to generate everything of the 248 example pages ... This is something for the user only :)
        editingService.getEditingFileManagementService().setStartIndex(243);

        // This already handles the database storage
        editingService.uploadNewEdition(testEdition, pdf);
        editingService.extractImagesFromPDF(testEdition);

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
        editingService.extractImagesFromPDF(testEdition);
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
}
