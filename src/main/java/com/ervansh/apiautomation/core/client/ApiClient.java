package com.ervansh.apiautomation.core.client;

import com.ervansh.apiautomation.core.auth.TokenManager;
import com.ervansh.apiautomation.core.auth.TokenRefreshFilter;
import com.ervansh.apiautomation.core.config.FrameworkConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/** Creates isolated REST Assured request specifications for individual operations. */
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

    /** Legacy static-token helper retained for simple bearer-only integrations. */
    public RequestSpecification authenticatedRequest() {
        return bearerTokenProvider.apply(request());
    }

    /** Authenticated request with managed login, refresh and one-time 401 retry. */
    public RequestSpecification authenticatedRequest(TokenManager tokenManager) {
        return request().filter(new TokenRefreshFilter(tokenManager));
    }
}
