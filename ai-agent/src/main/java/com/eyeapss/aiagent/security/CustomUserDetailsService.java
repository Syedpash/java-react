package com.eyeapss.aiagent.security;

import com.eyeapss.aiagent.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomUserDetailsService — loads a User from the database for Spring Security.
 *
 * Spring Security calls loadUserByUsername() during:
 *   1. Login — to verify the password
 *   2. JWT filter — to build the authentication object for each request
 *
 * Same pattern you built in TaskManager.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads user by username from DB and wraps in Spring Security's UserDetails.
     *
     * @param username the username to look up
     * @return UserDetails with username, hashed password, and roles
     * @throws UsernameNotFoundException if user doesn't exist in DB
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.eyeapss.aiagent.model.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert our User entity to Spring Security's UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
