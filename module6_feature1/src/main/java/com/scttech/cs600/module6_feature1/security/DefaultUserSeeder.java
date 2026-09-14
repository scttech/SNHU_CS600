package com.scttech.cs600.module6_feature1.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.scttech.cs600.module6_feature1.model.User;
import com.scttech.cs600.module6_feature1.repository.UserRepository;

/**
 * Seeds one sign-in-able user so the app is usable immediately after {@code docker compose up -d},
 * the same way earlier modules' single hardcoded credential worked out of the box. Dev-only, same
 * spirit as the Postgres credentials in application.properties — reuses those same
 * {@code app.security.username}/{@code app.security.password} values so the login screen accepts
 * the credentials students already know from Module 3 on.
 */
@Component
public class DefaultUserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultUsername;
    private final String defaultPassword;

    public DefaultUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
            @Value("${app.security.username}") String defaultUsername,
            @Value("${app.security.password}") String defaultPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultUsername = defaultUsername;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User(defaultUsername, passwordEncoder.encode(defaultPassword)));
        }
    }
}
