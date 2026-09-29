package com.eyeapss.aiagent.controller;

import com.eyeapss.aiagent.dto.AuthRequest;
import com.eyeapss.aiagent.dto.AuthResponse;
import com.eyeapss.aiagent.model.User;
import com.eyeapss.aiagent.repository.UserRepository;
import com.eyeapss.aiagent.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AuthController — handles user registration and login.
 *
 * POST /auth/register  — creates a new user account
 * POST /auth/login     — returns a JWT token for valid credentials
 *
 * Both endpoints are PUBLIC (no JWT needed) — defined in SecurityConfig.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user.
     *
     * POST /auth/register
     * Body: { "username": "alice", "password": "secret123" }
     * Response: { "message": "User registered successfully" }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        // Check if username already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username already exists"));
        }

        // Save user with BCrypt-hashed password
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        log.info("Registered new user: {}", request.getUsername());

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    /**
     * Login — authenticate and return JWT token.
     *
     * POST /auth/login
     * Body: { "username": "alice", "password": "secret123" }
     * Response: { "token": "eyJ...", "username": "alice" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        // authenticate() calls CustomUserDetailsService + BCrypt to verify credentials
        // Throws AuthenticationException if credentials are wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        // Credentials valid — generate JWT token
        String token = jwtUtil.generateToken(request.getUsername());
        log.info("User logged in: {}", request.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, request.getUsername()));
    }
}
