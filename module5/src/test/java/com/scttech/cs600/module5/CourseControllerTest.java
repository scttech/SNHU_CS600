package com.scttech.cs600.module5;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scttech.cs600.module5.config.SecurityConfig;
import com.scttech.cs600.module5.controller.CourseController;
import com.scttech.cs600.module5.model.Course;
import com.scttech.cs600.module5.repository.CourseRepository;

/**
 * Exercises {@link CourseController} in isolation: MockMvc drives real HTTP request handling
 * (routing, validation, JSON, status codes) while {@link CourseRepository} is mocked, so no
 * database is involved. {@link SecurityConfig} is imported explicitly because a plain
 * {@code @Configuration} class isn't picked up by {@code @WebMvcTest}'s restricted component scan
 * on its own — without it, these requests would hit Spring Boot's classpath-triggered default
 * security instead of our actual rules.
 */
@WebMvcTest(CourseController.class)
@Import(SecurityConfig.class)
class CourseControllerTest {

    // Must match app.security.username / app.security.password in application.properties, which
    // this test slice loads like the rest of the app's configuration.
    private static final String USERNAME = "student";
    private static final String PASSWORD = "changeit";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CourseRepository courseRepository;

    @Test
    void listsAllCourses() throws Exception {
        given(courseRepository.findAll()).willReturn(List.of(existingCourse(UUID.randomUUID(), "CS-600", "Software Design and Development", 3)));

        mockMvc.perform(authenticated(get("/api/courses")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].courseCode").value("CS-600"));
    }

    @Test
    void getsACourseById() throws Exception {
        UUID id = UUID.randomUUID();
        given(courseRepository.findById(id)).willReturn(Optional.of(existingCourse(id, "CS-600", "Software Design and Development", 3)));

        mockMvc.perform(authenticated(get("/api/courses/{id}", id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode").value("CS-600"))
                .andExpect(jsonPath("$.credits").value(3));
    }

    @Test
    void returns404WhenCourseIsMissing() throws Exception {
        UUID id = UUID.randomUUID();
        given(courseRepository.findById(id)).willReturn(Optional.empty());

        mockMvc.perform(authenticated(get("/api/courses/{id}", id)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsACourse() throws Exception {
        UUID savedId = UUID.randomUUID();
        Course toCreate = new Course("CS-610", "Algorithms", 3);
        given(courseRepository.save(any(Course.class))).willReturn(existingCourse(savedId, "CS-610", "Algorithms", 3));

        mockMvc.perform(authenticated(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/courses/" + savedId))
                .andExpect(jsonPath("$.id").value(savedId.toString()));
    }

    @Test
    void ignoresAClientSuppliedId() throws Exception {
        // Regression test: Swagger UI's "Try it out" example prefills every schema property,
        // including "id". If that value were allowed through, Spring Data JPA would treat the
        // course as already persisted and call merge() instead of persist(), which fails against
        // an id that doesn't exist yet. @JsonProperty(access = READ_ONLY) on Course.id is what
        // prevents Jackson from binding an incoming "id" at all.
        UUID savedId = UUID.randomUUID();
        given(courseRepository.save(any(Course.class))).willReturn(existingCourse(savedId, "CS-620", "New Course", 3));

        mockMvc.perform(authenticated(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"99999999-9999-9999-9999-999999999999\",\"courseCode\":\"CS-620\",\"title\":\"New Course\",\"credits\":3}")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedId.toString()));

        ArgumentCaptor<Course> saved = ArgumentCaptor.forClass(Course.class);
        verify(courseRepository).save(saved.capture());
        assertThat(saved.getValue().getId()).isNull();
    }

    @Test
    void rejectsAnInvalidCourse() throws Exception {
        Course invalid = new Course("", "", 0);

        mockMvc.perform(authenticated(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(courseRepository);
    }

    @Test
    void returns409ForADuplicateCourseCode() throws Exception {
        willThrow(new DataIntegrityViolationException("duplicate course_code"))
                .given(courseRepository).save(any(Course.class));

        mockMvc.perform(authenticated(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Course("CS-600", "Duplicate", 3)))))
                .andExpect(status().isConflict());
    }

    @Test
    void updatesAnExistingCourse() throws Exception {
        UUID id = UUID.randomUUID();
        given(courseRepository.findById(id)).willReturn(Optional.of(existingCourse(id, "CS-600", "Old Title", 3)));
        given(courseRepository.save(any(Course.class))).willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(authenticated(put("/api/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Course("CS-601", "New Title", 4)))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseCode").value("CS-601"))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.credits").value(4));
    }

    @Test
    void returns404WhenUpdatingAMissingCourse() throws Exception {
        UUID id = UUID.randomUUID();
        given(courseRepository.findById(id)).willReturn(Optional.empty());

        mockMvc.perform(authenticated(put("/api/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Course("CS-600", "Title", 3)))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletesACourse() throws Exception {
        UUID id = UUID.randomUUID();
        given(courseRepository.existsById(id)).willReturn(true);

        mockMvc.perform(authenticated(delete("/api/courses/{id}", id)))
                .andExpect(status().isNoContent());

        verify(courseRepository).deleteById(id);
    }

    @Test
    void returns404WhenDeletingAMissingCourse() throws Exception {
        UUID id = UUID.randomUUID();
        given(courseRepository.existsById(id)).willReturn(false);

        mockMvc.perform(authenticated(delete("/api/courses/{id}", id)))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns401WithoutCredentials() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(courseRepository);
    }

    @Test
    void returns401WithTheWrongPassword() throws Exception {
        mockMvc.perform(get("/api/courses").with(httpBasic(USERNAME, "not-the-password")))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(courseRepository);
    }

    private RequestBuilder authenticated(MockHttpServletRequestBuilder request) {
        return request.with(httpBasic(USERNAME, PASSWORD));
    }

    private Course existingCourse(UUID id, String courseCode, String title, int credits) {
        Course course = new Course(courseCode, title, credits);
        ReflectionTestUtils.setField(course, "id", id);
        return course;
    }
}
