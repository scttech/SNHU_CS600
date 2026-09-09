package com.scttech.cs600.module3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI courseCatalogOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Catalog API — Module 3")
                        .description("REST API for the CS-600 course catalog example, behind HTTP Basic auth")
                        .version("v1"))
                // Declares the "basicAuth" scheme Swagger UI's Authorize button uses. See
                // SecurityConfig for the real enforcement.
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
