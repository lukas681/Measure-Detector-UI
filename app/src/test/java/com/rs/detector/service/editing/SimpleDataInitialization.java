package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import com.rs.detector.domain.Project;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.repository.ProjectRepository;
import com.rs.detector.service.util.FileUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleDataInitialization {

    private final Logger log = LoggerFactory.getLogger(SimpleDataInitialization.class);

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EditionRepository editionRepository;

    @Autowired
    PageRepository pageRepository;

    Edition testEdition;
    Project testProject;
    Page testPage;

    public void setup() {
        log.debug("Starting initialization");
        // Restoring the whole database in parallel
        var c3 = pageRepository.deleteAll();
        var c1= editionRepository.deleteAll();
        var c2 = projectRepository.deleteAll();

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

        projectRepository.insert(testProject).block();
        editionRepository.insert(testEdition).block();
        pageRepository.insert(testPage).block();
        System.out.println("Populated Database with Testobjects");
    }
}
