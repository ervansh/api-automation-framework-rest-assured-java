package com.ervansh.apiautomation.services.products;

import com.ervansh.apiautomation.core.client.ApiClient;
import com.ervansh.apiautomation.services.products.model.Product;
import io.restassured.response.Response;

/**
 * Service-specific operations kept separate from scenario assertions.
 * The endpoint paths are illustrative and must be aligned to a real API contract.
 */
public final class ProductApiClient {
    private final ApiClient apiClient;

    public ProductApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response getById(String productId) {
        return apiClient.request()
                .pathParam("productId", productId)
                .when()
                .get("/products/{productId}");
    }

    public Response create(Product product) {
        return apiClient.request()
                .body(product)
                .when()
                .post("/products");
    }
}
