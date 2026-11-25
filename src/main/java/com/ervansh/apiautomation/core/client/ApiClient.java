package com.ervansh.apiautomation.core.client;

import com.ervansh.apiautomation.core.config.FrameworkConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Creates an isolated REST Assured request specification for one operation.
 * Service clients should build endpoint-specific requests on top of this class.
 */
public final class ApiClient {
    private final FrameworkConfig config;
    private final BearerTokenProvider bearerTokenProvider;

    public ApiClient(FrameworkConfig config) {
        this(config, new BearerTokenProvider());
    }

    ApiClient(FrameworkConfig config, BearerTokenProvider bearerTokenProvider) {
        this.config = config;
        this.bearerTokenProvider = bearerTokenProvider;
    }

    public RequestSpecification request() {
        HttpClientConfig httpClientConfig = HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMillis())
                .setParam("http.socket.timeout", config.readTimeoutMillis());

        return given()
                .baseUri(config.baseUri())
                .header("Accept", "application/json")
                .contentType("application/json")
                .config(io.restassured.config.RestAssuredConfig.config()
                        .httpClient(httpClientConfig));
    }

    /** Applies a static bearer token supplied through environment/JVM configuration. */
    public RequestSpecification authenticatedRequest() {
        return bearerTokenProvider.apply(request());
    }

    /** Applies managed bearer authentication with a single 401 refresh-and-retry. */
    public RequestSpecification authenticatedRequest(
            com.ervansh.apiautomation.core.auth.TokenManager tokenManager) {
        return request().filter(new com.ervansh.apiautomation.core.auth.TokenRefreshFilter(tokenManager));
    }
}
