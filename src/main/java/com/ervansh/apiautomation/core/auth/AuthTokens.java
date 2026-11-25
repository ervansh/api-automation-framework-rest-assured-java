package com.ervansh.apiautomation.core.auth;

public record AuthTokens(String accessToken, String refreshToken) {
    public AuthTokens {
        if (accessToken == null || accessToken.isBlank() || refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Both access and refresh tokens are required");
        }
    }
}