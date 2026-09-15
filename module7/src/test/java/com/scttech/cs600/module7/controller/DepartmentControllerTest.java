package com.scttech.cs600.module7.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.scttech.cs600.module7.config.ApiSecurityConfig;
import com.scttech.cs600.module7.model.User;
import com.scttech.cs600.module7.model.department.Department;
import com.scttech.cs600.module7.repository.DepartmentRepository;
import com.scttech.cs600.module7.repository.UserRepository;
import com.scttech.cs600.module7.security.DomainUserDetailsService;

/**
 * Exercises {@link DepartmentController} in isolation: MockMvc drives real HTTP request handling
 * (routing, JSON, status codes) while {@link DepartmentRepository} and {@link UserRepository} are
 * mocked, so no database is involved. {@link ApiSecurityConfig} and
 * {@link DomainUserDetailsService} are imported explicitly because neither a plain
 * {@code @Configuration} class nor a {@code @Service} is picked up by {@code @WebMvcTest}'s
 * restricted component scan on its own — without them, these requests would hit Spring Boot's
 * classpath-triggered default security instead of our actual rules.
 */
@WebMvcTest(DepartmentController.class)
@Import({ ApiSecurityConfig.class, DomainUserDetailsService.class })
class DepartmentControllerTest {

    // Arbitrary, self-consistent credentials for this test slice — DomainUserDetailsService looks
    // them up via the mocked UserRepository below, never the real `users` table or
    // application.properties.
    private static final String USERNAME = "student";
    private static final String PASSWORD = "changeit";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private DepartmentRepository departmentRepository;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    void stubExistingUser() {
        given(userRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(new User(USERNAME, passwordEncoder.encode(PASSWORD))));
    }

    @Test
    void listsAllDepartments() throws Exception {
        given(departmentRepository.findAll()).willReturn(List.of(new Department("CS", "Computer Science", "CS Department")));

        mockMvc.perform(authenticated(get("/api/departments")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].code").value("CS"))
                .andExpect(jsonPath("$[0].name").value("Computer Science"));
    }

    @Test
    void returns401WithoutCredentials() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(departmentRepository);
    }

    @Test
    void returns401WithTheWrongPassword() throws Exception {
        mockMvc.perform(get("/api/departments").with(httpBasic(USERNAME, "not-the-password")))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(departmentRepository);
    }

    private RequestBuilder authenticated(MockHttpServletRequestBuilder request) {
        return request.with(httpBasic(USERNAME, PASSWORD));
    }
}
