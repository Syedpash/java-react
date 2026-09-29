package com.eyeapss.aiagent.config;

import com.eyeapss.aiagent.security.CustomUserDetailsService;
import com.eyeapss.aiagent.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * SecurityConfig — configures Spring Security for the AI Agent.
 *
 * PUBLIC endpoints (no JWT needed):
 *   POST /auth/register  — create account
 *   POST /auth/login     — get JWT token
 *   GET  /h2-console/**  — H2 DB console for development
 *
 * PROTECTED endpoints (JWT required):
 *   POST /agent/chat     — chat with agent
 *   GET  /agent/history  — conversation history
 *   POST /agent/clear    — clear memory
 *
 * Same pattern you used in TaskManager's SecurityConfig.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Main security filter chain.
     * Defines which endpoints are public, which are protected,
     * and how sessions + CORS + CSRF are handled.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless REST + JWT
            .csrf(AbstractHttpConfigurer::disable)

            // CORS — allow requests from Next.js frontend on localhost:3000
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Define public vs protected routes
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()       // login/register
                .requestMatchers("/h2-console/**").permitAll() // H2 console (dev only)
                .anyRequest().authenticated()                  // everything else needs JWT
            )

            // STATELESS — no server-side sessions, JWT is the only auth mechanism
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Allow H2 console to render in iframes (it uses framesets)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // Register our JWT filter BEFORE the default username/password filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCrypt password encoder — slow hashing algorithm, secure for passwords.
     * Never store plain text passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authentication provider — wires UserDetailsService + PasswordEncoder.
     * Used by AuthenticationManager during login to verify credentials.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager — used in AuthController to authenticate login requests.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * CORS configuration — allows the Next.js frontend (localhost:3000) to call this API.
     * Without this, browsers block cross-origin requests.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Next.js dev server
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
