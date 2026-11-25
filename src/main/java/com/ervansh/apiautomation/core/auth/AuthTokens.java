package com.ervansh.apiautomation.core.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthTokens(String accessToken, String refreshToken) {
    public AuthTokens {
        if (accessToken == null || accessToken.isBlank() || refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Both access and refresh tokens are required");
        }
    }
}
