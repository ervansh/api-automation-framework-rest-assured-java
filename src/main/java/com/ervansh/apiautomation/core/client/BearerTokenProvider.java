package com.ervansh.apiautomation.core.client;

import io.restassured.specification.RequestSpecification;

/**
 * Applies bearer authentication without persisting or logging the credential.
 * Provide the token via -Dapi.bearerToken or API_BEARER_TOKEN.
 */
public final class BearerTokenProvider {
    private static final String SYSTEM_PROPERTY = "api.bearerToken";
    private static final String ENVIRONMENT_VARIABLE = "API_BEARER_TOKEN";

    public RequestSpecification apply(RequestSpecification request) {
        String token = System.getProperty(SYSTEM_PROPERTY);
        if (token == null || token.isBlank()) {
            token = System.getenv(ENVIRONMENT_VARIABLE);
        }
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Bearer token is required; set -D" + SYSTEM_PROPERTY + " or " + ENVIRONMENT_VARIABLE);
        }
        return request.header("Authorization", "Bearer " + token);
    }
}
