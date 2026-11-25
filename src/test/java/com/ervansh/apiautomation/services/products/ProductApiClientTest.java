package com.ervansh.apiautomation.services.products;

import com.ervansh.apiautomation.core.client.ApiClient;
import com.ervansh.apiautomation.core.config.FrameworkConfig;
import com.ervansh.apiautomation.services.products.model.Product;
import com.sun.net.httpserver.HttpServer;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Service-client contract smoke tests against an in-process HTTP stub.
 * The stub is test-owned and does not represent a production service.
 */
public class ProductApiClientTest {
    private HttpServer server;
    private ProductApiClient products;

    @BeforeClass
    public void startStub() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/products/sku-42", exchange -> {
            byte[] body = """
                    {"id":"sku-42","name":"Test product","priceInCents":1299}
                    """.strip().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.createContext("/products", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            byte[] body = """
                    {"id":"sku-new","name":"Created product","priceInCents":2500}
                    """.strip().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(201, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();

        ApiClient apiClient = new ApiClient(new FrameworkConfig(
                "http://127.0.0.1:" + server.getAddress().getPort(), 2_000, 2_000));
        products = new ProductApiClient(apiClient);
    }

    @AfterClass(alwaysRun = true)
    public void stopStub() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void getByIdReturnsProductRepresentation() {
        Response response = products.getById("sku-42");

        assertEquals(response.statusCode(), 200);
        assertEquals(response.jsonPath().getString("id"), "sku-42");
        assertEquals(response.jsonPath().getString("name"), "Test product");
        assertEquals(response.jsonPath().getInt("priceInCents"), 1299);
    }

    @Test
    public void createSendsProductPayloadAndReturnsCreatedStatus() {
        Response response = products.create(new Product(null, "Created product", 2500));

        assertEquals(response.statusCode(), 201);
        assertEquals(response.jsonPath().getString("id"), "sku-new");
        assertEquals(response.jsonPath().getInt("priceInCents"), 2500);
        assertTrue(response.contentType().contains("application/json"));
    }
}
