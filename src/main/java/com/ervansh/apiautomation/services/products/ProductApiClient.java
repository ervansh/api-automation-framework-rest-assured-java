package com.ervansh.apiautomation.services.products;

import com.ervansh.apiautomation.core.auth.TokenManager;
import com.ervansh.apiautomation.core.auth.TokenRefreshFilter;
import com.ervansh.apiautomation.core.client.ApiClient;
import com.ervansh.apiautomation.services.products.model.CreateProductRequest;
import io.restassured.response.Response;

public final class ProductApiClient {
    private final ApiClient api;
    private final TokenManager tokenManager;

    public ProductApiClient(ApiClient api, TokenManager tokenManager) {
        this.api = api;
        this.tokenManager = tokenManager;
    }

    public Response getById(int productId) {
        return api.request().pathParam("productId", productId).get("/products/{productId}");
    }

    public Response getAll(int limit, int skip) {
        return api.request().queryParam("limit", limit).queryParam("skip", skip).get("/products");
    }

    public Response search(String query) {
        return api.request().queryParam("q", query).get("/products/search");
    }

    public Response create(CreateProductRequest product) {
        return api.request().body(product).post("/products/add");
    }

    public Response getCurrentUser() {
        return api.request().filter(new TokenRefreshFilter(tokenManager)).get("/auth/me");
    }
}
