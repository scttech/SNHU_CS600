package com.scttech.cs600.module6_feature1.cucumber;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.scttech.cs600.module6_feature1.cucumber.TestHttpClient.Response;
import com.scttech.cs600.module6_feature1.model.User;
import com.scttech.cs600.module6_feature1.repository.UserRepository;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for src/test/resources/features/login.feature. Drives the real, running app
 * (see {@link CucumberSpringConfiguration}) over HTTPS the way a browser eventually would —
 * without a browser (see the "Test approach" discussion in
 * docs/module6_feature1/README.md#agile) — so every assertion here is about the actual Spring
 * Security contract: redirects, session cookies, the {@code users} table, and the REST API.
 */
public class LoginStepDefinitions {

    private static final Pattern CSRF_META_TAG = Pattern.compile("<meta name=\"_csrf\" content=\"([^\"]*)\"");

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

    @When("I request the Courses view without signing in")
    public void i_request_the_courses_view_without_signing_in() throws IOException {
        lastResponse = http.get(baseUrl() + "/", Map.of());
    }

    @When("I sign in as {string} with password {string}")
    public void i_sign_in_as_with_password(String username, String password) throws IOException {
        // GET /login first: establishes a session and reads the Spring Security CSRF token that
        // Vaadin embeds as a <meta name="_csrf" content="..."> tag in the bootstrap page (see
        // com.vaadin.flow.internal.springcsrf.SpringCsrfTokenUtil) — the same token the real
        // LoginForm's rendered <form> carries as a hidden field.
        Response loginPage = http.get(baseUrl() + "/login", Map.of());
        String anonymousSessionCookie = firstCookie(loginPage, "JSESSIONID");
        String csrfToken = extractCsrfToken(loginPage.body());

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Cookie", anonymousSessionCookie);
        String formBody = TestHttpClient.formEncode(Map.of(
                "username", username,
                "password", password,
                "_csrf", csrfToken));

        lastResponse = http.postForm(baseUrl() + "/login", headers, formBody);

        // Spring Security issues a fresh session id on successful authentication (session-fixation
        // protection), so the cookie to use for follow-up requests may have changed; if the
        // response didn't set a new one (e.g. a failed attempt), the pre-login session is still
        // the right one to keep using.
        String postLoginCookie = firstCookie(lastResponse, "JSESSIONID");
        sessionCookie = postLoginCookie != null ? postLoginCookie : anonymousSessionCookie;
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

    @Then("I can request the Courses view without being redirected")
    public void i_can_request_the_courses_view_without_being_redirected() throws IOException {
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

    private String baseUrl() {
        return "https://localhost:" + port;
    }

    private static String firstCookie(Response response, String cookieName) {
        return response.headerValues("Set-Cookie").stream()
                .filter(cookie -> cookie.startsWith(cookieName + "="))
                .map(cookie -> cookie.split(";", 2)[0])
                .findFirst()
                .orElse(null);
    }

    private static String extractCsrfToken(String loginPageHtml) {
        Matcher matcher = CSRF_META_TAG.matcher(loginPageHtml);
        if (!matcher.find()) {
            throw new IllegalStateException("No _csrf meta tag found on the login page");
        }
        return matcher.group(1);
    }
}
