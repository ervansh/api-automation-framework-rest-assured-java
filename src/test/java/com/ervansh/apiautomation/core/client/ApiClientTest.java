package com.ervansh.apiautomation.core.client;

import com.ervansh.apiautomation.core.config.FrameworkConfig;
import com.sun.net.httpserver.HttpServer;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Framework self-test against an in-process HTTP endpoint.
 * This verifies the shared request setup without depending on a target API.
 */
public class ApiClientTest {
    private HttpServer server;
    private RequestSpecification request;

    @BeforeClass
    public void startLocalServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/health", exchange -> {
            byte[] response = "{\"status\":\"UP\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            try (var output = exchange.getResponseBody()) {
                output.write(response);
            }
        });
        server.start();

        FrameworkConfig config = new FrameworkConfig(
                "http://127.0.0.1:" + server.getAddress().getPort(),
                2_000,
                2_000);
        request = new ApiClient(config).request();
    }

    @AfterClass(alwaysRun = true)
    public void stopLocalServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void requestSpecificationCanCallJsonEndpoint() {
        var response = request.when().get("/health");

        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.jsonPath().getString("status"), equalTo("UP"));
    }
}
