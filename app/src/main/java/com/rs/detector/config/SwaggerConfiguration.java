package com.rs.detector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;
import tech.jhipster.config.JHipsterConstants;

@Primary
@Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.OAS_30)
            .select() //
            .apis(RequestHandlerSelectors.basePackage("com.rs.detector.web.api") )//
//                .or(RequestHandlerSelectors.basePackage("com.rs.detector.web.api"))) // use or here, not and
            .paths(PathSelectors.any())
            .build().groupName("openAPI");
        return docket;
    }
}
