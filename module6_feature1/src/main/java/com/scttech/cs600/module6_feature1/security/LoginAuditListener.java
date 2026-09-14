package com.scttech.cs600.module6_feature1.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.scttech.cs600.module6_feature1.repository.UserRepository;

/**
 * The audit hook this story exists to build: every time someone's credentials are successfully
 * verified — through the Vaadin login form or the REST API's HTTP Basic auth, both of which share
 * the one {@link DomainUserDetailsService} — their {@code users} row's {@code last_login_at} is
 * updated (see docs/module6_feature1/ddd/login-sequence.puml). Because the REST API re-validates
 * Basic auth credentials on every request, this also updates on every authenticated API call, not
 * just interactive sign-in — "most recently verified", not strictly "most recently logged in".
 * Distinguishing the two is future work, not something this story needs.
 */
@Component
public class LoginAuditListener {

    private final UserRepository userRepository;

    public LoginAuditListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        userRepository.findByUsername(event.getAuthentication().getName()).ifPresent(user -> {
            user.recordSuccessfulLogin();
            userRepository.save(user);
        });
    }
}
