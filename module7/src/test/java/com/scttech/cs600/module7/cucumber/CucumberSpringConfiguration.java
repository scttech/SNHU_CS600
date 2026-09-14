package com.scttech.cs600.module7.cucumber;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.cucumber.spring.CucumberContextConfiguration;

/**
 * Wires Cucumber's step definitions into the same kind of Spring context (and the same
 * Testcontainers Postgres) {@code CourseRepositoryTest} uses, rather than a mocked slice — these
 * scenarios are meant to exercise the real Spring Security filter chains end to end. Every step
 * definition class in this package shares this one context.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CucumberSpringConfiguration {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
}
