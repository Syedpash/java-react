package com.eyeapss.aiagent.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter — intercepts every HTTP request and validates the JWT token.
 *
 * FLOW for each request:
 *   1. Extract "Authorization: Bearer <token>" header
 *   2. Parse the username from the token
 *   3. Load the user from DB via UserDetailsService
 *   4. Validate token against the user
 *   5. Set authentication in SecurityContextHolder
 *   6. Continue to next filter / controller
 *
 * Extends OncePerRequestFilter — guaranteed to run exactly once per request.
 * Same pattern you built in TaskManager.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Get the Authorization header
        String authHeader = request.getHeader("Authorization");

        // If no header or doesn't start with "Bearer ", skip JWT auth
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2: Extract token (strip "Bearer " prefix)
        String token = authHeader.substring(7);
        String username = null;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            log.warn("Failed to extract username from JWT: {}", e.getMessage());
        }

        // Step 3 & 4: Validate and set authentication
        // Only if username extracted AND no authentication already set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                // Step 5: Create authentication token and set in SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authenticated user: {}", username);
            }
        }

        // Step 6: Continue filter chain
        filterChain.doFilter(request, response);
    }
}
