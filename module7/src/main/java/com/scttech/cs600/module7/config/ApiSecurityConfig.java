package com.scttech.cs600.module7.config;

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
 * Everything under {@code /api/**} stays behind stateless HTTP Basic auth, checked against the
 * {@code users} table via {@link com.scttech.cs600.module7.security.DomainUserDetailsService} —
 * the same one {@link WebSecurityConfig} uses for the Vaadin login screen, so there's one source
 * of truth for credentials. Deny-by-default: a new {@code @RestController} is secured the moment
 * it's added, with no matching list to remember to update. Any endpoint that should be public
 * (health checks, a webhook, etc.) gets an explicit {@code permitAll()} matcher ahead of the
 * catch-all rule below, not a new controller-specific chain.
 *
 * <p>Kept in its own {@code @Configuration} class, separate from {@link WebSecurityConfig}, so a
 * {@code @WebMvcTest} slice for a REST controller (see {@code CourseControllerTest}) can import
 * just this and not need Vaadin's Spring integration on its classpath at all.
 */
@Configuration
@EnableWebSecurity
public class ApiSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth
                        // .requestMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated())
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
