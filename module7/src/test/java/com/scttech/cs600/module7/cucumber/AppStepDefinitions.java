package com.scttech.cs600.module7.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scttech.cs600.module7.cucumber.TestHttpClient.Response;
import com.scttech.cs600.module7.model.user.User;
import com.scttech.cs600.module7.repository.user.UserRepository;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for both src/test/resources/features/login.feature and dashboard.feature.
 * Cucumber's glue is global — every {@code .feature} file in this module shares one step-text
 * namespace regardless of which class a step is implemented in — so the two features live in one
 * class here rather than two, which would either collide on identical step text (an ambiguous-step
 * error, not a warning) or need a scenario-scoped bean just to share {@code sessionCookie} between
 * a sign-in step and a later assertion. Drives the real, running app (see
 * {@link CucumberSpringConfiguration}) over HTTPS the way a browser eventually would, without a
 * browser — see the "Test approach" discussion in docs/module6_feature1/README.md#agile.
 */
public class AppStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final TestHttpClient http = new TestHttpClient();

    private Response lastResponse;
    private String sessionCookie;

    @Given("a user {string} with password {string} exists")
    public void a_user_with_password_exists(String username, String password) {
        userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.save(new User(username, passwordEncoder.encode(password))));
    }

    @When("I sign in as {string} with password {string}")
    public void i_sign_in_as_with_password(String username, String password) throws IOException {
        TestHttpClient.SignInResult result = http.signIn(baseUrl(), username, password);
        lastResponse = result.response();
        sessionCookie = result.sessionCookie();
    }

    // --- login.feature: authentication mechanics; which view lands at "/" is not this file's
    // concern (see dashboard.feature below) ---

    @When("I request the app without signing in")
    public void i_request_the_app_without_signing_in() throws IOException {
        lastResponse = http.get(baseUrl() + "/", Map.of());
    }

    @When("I call the courses API without credentials")
    public void i_call_the_courses_api_without_credentials() throws IOException {
        lastResponse = http.get(baseUrl() + "/api/courses", Map.of());
    }

    @When("I call the courses API as {string} with password {string}")
    public void i_call_the_courses_api_as_with_password(String username, String password) throws IOException {
        lastResponse = http.get(baseUrl() + "/api/courses",
                Map.of("Authorization", TestHttpClient.basicAuthHeader(username, password)));
    }

    @Then("I am redirected to the login screen")
    public void i_am_redirected_to_the_login_screen() {
        assertThat(lastResponse.status()).isBetween(300, 399);
        assertThat(lastResponse.header("Location")).contains("/login");
    }

    @Then("my sign-in succeeds")
    public void my_sign_in_succeeds() {
        assertThat(lastResponse.status()).isBetween(300, 399);
        assertThat(lastResponse.header("Location")).doesNotContain("error");
    }

    @Then("I can request the app without being redirected")
    public void i_can_request_the_app_without_being_redirected() throws IOException {
        Response response = http.get(baseUrl() + "/", Map.of("Cookie", sessionCookie));
        assertThat(response.status()).isEqualTo(200);
    }

    @Then("I am sent back to the login screen with an error")
    public void i_am_sent_back_to_the_login_screen_with_an_error() {
        assertThat(lastResponse.status()).isBetween(300, 399);
        String location = lastResponse.header("Location");
        assertThat(location).contains("/login");
        assertThat(location).contains("error");
    }

    @Then("{string}'s last sign-in time is recorded")
    public void s_last_sign_in_time_is_recorded(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        assertThat(user.getLastLoginAt()).isNotNull();
        assertThat(user.getLastLoginAt()).isAfter(Instant.now().minusSeconds(30));
    }

    @Then("the API responds with status {int}")
    public void the_api_responds_with_status(int status) {
        assertThat(lastResponse.status()).isEqualTo(status);
    }

    // --- dashboard.feature: which view owns "" vs. "/courses" ---

    @When("I request the Course Catalog without signing in")
    public void i_request_the_course_catalog_without_signing_in() throws IOException {
        lastResponse = http.get(baseUrl() + "/courses", Map.of());
    }

    @Then("I am redirected to the Dashboard")
    public void i_am_redirected_to_the_dashboard() {
        assertThat(lastResponse.status()).isBetween(300, 399);
        assertThat(TestHttpClient.pathOf(lastResponse.header("Location"))).isEqualTo("/");
    }

    @Then("I can request the Course Catalog without being redirected")
    public void i_can_request_the_course_catalog_without_being_redirected() throws IOException {
        Response response = http.get(baseUrl() + "/courses", Map.of("Cookie", sessionCookie));
        assertThat(response.status()).isEqualTo(200);
    }

    private String baseUrl() {
        return "https://localhost:" + port;
    }
}
