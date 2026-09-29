package com.eyeapss.aiagent.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtil — generates and validates JWT tokens.
 *
 * JWT structure: header.payload.signature
 *   header:    { "alg": "HS256", "typ": "JWT" }
 *   payload:   { "sub": "alice", "iat": 1234567890, "exp": 1234654290 }
 *   signature: HMAC-SHA256(header + "." + payload, secretKey)
 *
 * Same pattern you used in TaskManager's JwtUtil.
 */
@Component
@Slf4j
public class JwtUtil {

    // Secret key from application.yml — must be at least 256 bits for HS256
    @Value("${agent.jwt.secret:agentSecretKey12345678901234567890123456789012}")
    private String secret;

    // Token expiry — default 24 hours in milliseconds
    @Value("${agent.jwt.expiration:86400000}")
    private long expiration;

    /**
     * Generates a signing key from the secret string.
     * Keys.hmacShaKeyFor() creates a secure key for HMAC-SHA256.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given username.
     *
     * @param username the authenticated user's username
     * @return signed JWT token string
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)                          // "sub" claim
                .setIssuedAt(new Date())                       // "iat" claim
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // "exp" claim
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token JWT token string
     * @return username stored in the token
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Validates a token — checks signature and expiry.
     *
     * @param token    JWT token string
     * @param username expected username
     * @return true if valid and not expired
     */
    public boolean validateToken(String token, String username) {
        try {
            String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
