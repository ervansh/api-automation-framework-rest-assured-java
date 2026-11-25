package com.ervansh.apiautomation.core.client;

import com.ervansh.apiautomation.core.config.FrameworkConfig;
import com.sun.net.httpserver.HttpServer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;

public class BearerTokenProviderTest {
    private static final String TOKEN_PROPERTY = "api.bearerToken";
    private HttpServer server;
    private ApiClient client;

    @BeforeMethod
    public void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/secure", exchange -> {
            String authorization = exchange.getRequestHeaders().getFirst("Authorization");
            int status = "Bearer test-token".equals(authorization) ? 200 : 401;
            byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        client = new ApiClient(new FrameworkConfig(
                "http://127.0.0.1:" + server.getAddress().getPort(), 2_000, 2_000));
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        System.clearProperty(TOKEN_PROPERTY);
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void authenticatedRequestAddsBearerAuthorizationHeader() {
        System.setProperty(TOKEN_PROPERTY, "test-token");

        int status = client.authenticatedRequest().get("/secure").statusCode();

        assertEquals(status, 200);
    }

    @Test
    public void authenticatedRequestFailsClearlyWhenTokenIsMissing() {
        System.clearProperty(TOKEN_PROPERTY);

        org.testng.Assert.assertThrows(IllegalStateException.class, client::authenticatedRequest);
    }
}
