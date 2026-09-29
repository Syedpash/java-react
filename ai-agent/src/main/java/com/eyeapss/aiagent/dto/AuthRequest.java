package com.eyeapss.aiagent.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** POST /auth/register and POST /auth/login request body */
@Data
public class AuthRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
