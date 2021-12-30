package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.MeasureBox;
import com.rs.detector.domain.Page;
import com.rs.detector.domain.Project;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.repository.MeasureBoxRepository;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.repository.ProjectRepository;
import com.rs.detector.service.util.FileUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SimpleDataInitialization {

    private final Logger log = LoggerFactory.getLogger(SimpleDataInitialization.class);

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EditionRepository editionRepository;

    @Autowired
    MeasureBoxRepository measureBoxRepository;

    @Autowired
    PageRepository pageRepository;

    public Edition testEdition;
    public Project testProject;
    public Page testPage;
    public List<MeasureBox> testBoxes;

    public void setup() {
        log.debug("Starting initialization");
        // Restoring the whole database in parallel, Maybe not even required as we use in-memory h2 database for testing
        var c4 = measureBoxRepository.deleteAll();
        var c3 = pageRepository.deleteAll();
        var c1= editionRepository.deleteAll();
        var c2 = projectRepository.deleteAll();

        c4.block();
        c3.block();
        c1.block();
        c2.block();

        testProject = new Project()
            .id(1l) // Should be automatically done by deleting whole database.
            .name("TestProject")
            .composer("Richi Strauß");

        testEdition = new Edition()
            .id(1l)
            .title("testTitle")
            .pDFFileName("aegyptische-helena.pdf")
            .project(testProject);

        testPage = new Page()
            .id(1l)
            .edition(testEdition)
            .pageNr(245l);

        testBoxes = new ArrayList<>() {
            {
                add(new MeasureBox()
                    .page(testPage)
                    .lrx(123l)
                    .lry(123l)
                    .ulx(23l)
                    .uly(123l)
                );
                add(new MeasureBox()
                    .page(testPage)
                    .lrx(223l)
                    .lry(324l)
                    .ulx(146l)
                    .uly(176l)
                );
                add(new MeasureBox()
                    .page(testPage)
                    .lrx(246l)
                    .lry(246l)
                    .ulx(146l)
                    .uly(4354l)
                );
            }
        };

        projectRepository.insert(testProject).block();
        editionRepository.insert(testEdition).block();
        pageRepository.insert(testPage).block();
        testBoxes.forEach(x-> measureBoxRepository.insert(x).block());
        System.out.println("Populated Database with Testobjects");
    }
}
