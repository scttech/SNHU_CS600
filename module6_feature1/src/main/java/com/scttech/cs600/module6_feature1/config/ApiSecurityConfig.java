package com.scttech.cs600.module6_feature1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * {@code /api/courses/**} stays behind stateless HTTP Basic auth, checked against the
 * {@code users} table via
 * {@link com.scttech.cs600.module6_feature1.security.DomainUserDetailsService} — the same one
 * {@link WebSecurityConfig} uses for the Vaadin login screen, so there's one source of truth for
 * credentials. Kept in its own {@code @Configuration} class, separate from
 * {@link WebSecurityConfig}, so a {@code @WebMvcTest} slice for {@code CourseController} (see
 * {@code CourseControllerTest}) can import just this and not need Vaadin's Spring integration on
 * its classpath at all.
 */
@Configuration
@EnableWebSecurity
public class ApiSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/courses/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
