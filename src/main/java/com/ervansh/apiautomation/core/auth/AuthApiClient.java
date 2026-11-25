package com.ervansh.apiautomation.core.auth;

import com.ervansh.apiautomation.core.client.ApiClient;
import io.restassured.response.Response;

import java.util.Map;

public final class AuthApiClient {
    private final ApiClient api;

    public AuthApiClient(ApiClient api) { this.api = api; }

    public Response login(String username, String password, int expiresInMins) {
        return api.request().body(Map.of("username", username, "password", password,
                "expiresInMins", expiresInMins)).post("/auth/login");
    }

    public Response refresh(String refreshToken, int expiresInMins) {
        return api.request().body(Map.of("refreshToken", refreshToken,
                "expiresInMins", expiresInMins)).post("/auth/refresh");
    }

    public Response currentUser(String accessToken) {
        return api.request().header("Authorization", "Bearer " + accessToken).get("/auth/me");
    }
}