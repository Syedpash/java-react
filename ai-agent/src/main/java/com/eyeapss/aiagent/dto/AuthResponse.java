package com.eyeapss.aiagent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Response from /auth/login — contains the JWT token */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String username;
}
