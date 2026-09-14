package com.scttech.cs600.module6_feature2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.scttech.cs600.module6_feature2.ui.LoginView;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

/**
 * Everything that isn't {@code /api/courses/**} (see {@link ApiSecurityConfig}) — the Vaadin UI,
 * Swagger UI, actuator, the OpenAPI doc itself — now sits behind {@link LoginView} instead of
 * being wide open. {@link VaadinSecurityConfigurer} wires up form login against
 * {@link com.scttech.cs600.module6_feature2.security.DomainUserDetailsService}, permits Vaadin's
 * own static/internal requests, and sends unauthenticated navigation to {@link LoginView}.
 */
@Configuration
public class WebSecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        // A 401/403/500 anywhere in the app — including from ApiSecurityConfig's own
        // BasicAuthenticationEntryPoint calling response.sendError() for an unauthenticated
        // /api/courses request — makes the servlet container internally forward to Spring Boot's
        // /error endpoint. That forward is a brand new request, which (without this line) also
        // has to pass this same chain's "any request must be authenticated" rule below, and an
        // anonymous one fails it — replacing the original 401 with this chain's own redirect to
        // /login before the client ever sees it. Permitting /error keeps the original response.
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/error").permitAll());
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer.loginView(LoginView.class));
        return http.build();
    }
}
