package com.scttech.cs600.module2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseCatalogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Catalog API — Module 2")
                        .description("REST API for the CS-600 course catalog example")
                        .version("v1"));
    }
}
