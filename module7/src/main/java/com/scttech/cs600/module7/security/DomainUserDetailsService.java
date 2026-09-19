package com.scttech.cs600.module7.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scttech.cs600.module7.model.user.User;
import com.scttech.cs600.module7.repository.user.UserRepository;

/**
 * Bridges Spring Security's authentication mechanism to the {@link User} aggregate — both the
 * Vaadin login form (see WebSecurityConfig) and the REST API's HTTP Basic auth (see
 * ApiSecurityConfig) go through this, so there's exactly one source of truth for credentials (see
 * docs/module7/ddd/bounded-context.puml).
 */
@Service
public class DomainUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!user.isEnabled())
                .roles("USER")
                .build();
    }
}
