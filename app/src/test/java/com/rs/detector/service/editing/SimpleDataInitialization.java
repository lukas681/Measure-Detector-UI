package com.rs.detector.service.editing;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Project;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimpleDataInitialization {


    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    EditionRepository editionRepository;

    Edition testEdition;
    Project testProject;

    public void setup() {
        testProject = new Project()
            .id(1l)
            .name("TestProject")
            .composer("Richi Strauß");

        testEdition = new Edition()
            .id(1l)
            .title("testTitle")
            .pDFFileName("aegyptische-helena.pdf")
            .project(testProject);


        projectRepository.save(testProject);
        editionRepository.save(testEdition);
        System.out.println("Populated Database with Testobjects");
    }
}
