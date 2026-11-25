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

    public ApiClient(FrameworkConfig config) {
        this.config = config;
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
}
