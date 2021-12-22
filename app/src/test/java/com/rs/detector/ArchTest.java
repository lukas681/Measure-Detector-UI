package com.rs.detector;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    // TODO Fix and investigate
    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.rs.detector");

        noClasses()
            .that()
            .resideInAnyPackage("com.rs.detector.service..")
            .or()
            .resideInAnyPackage("com.rs.detector.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.rs.detector.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
