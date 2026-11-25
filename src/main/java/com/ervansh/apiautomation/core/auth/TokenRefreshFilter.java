package com.ervansh.apiautomation.core.auth;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/** Retries an authenticated request once after HTTP 401; never loops on repeated failures. */
public final class TokenRefreshFilter implements Filter {
    private final TokenManager tokenManager;

    public TokenRefreshFilter(TokenManager tokenManager) { this.tokenManager = tokenManager; }

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpecification,
                           FilterContext context) {
        String previousToken = tokenManager.accessToken();
        request.replaceHeader("Authorization", "Bearer " + previousToken);
        Response response = context.next(request, responseSpecification);
        if (response.statusCode() != 401) return response;

        String refreshedToken = tokenManager.refreshAccessToken(previousToken);
        request.replaceHeader("Authorization", "Bearer " + refreshedToken);
        return context.next(request, responseSpecification);
    }
}