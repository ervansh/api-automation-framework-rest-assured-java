package com.ervansh.apiautomation.core.client;

import io.restassured.specification.RequestSpecification;

/** Reusable request defaults. Return a fresh specification per request. */
public final class RequestSpecFactory {
    private final ApiClient apiClient;

    public RequestSpecFactory(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public RequestSpecification json() {
        return apiClient.request()
                .accept("application/json")
                .contentType("application/json");
    }

    public RequestSpecification authenticatedJson() {
        return apiClient.authenticatedRequest()
                .accept("application/json")
                .contentType("application/json");
    }
}
