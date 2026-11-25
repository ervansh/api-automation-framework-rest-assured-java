package com.ervansh.apiautomation.services.products;

import com.ervansh.apiautomation.core.auth.TokenManager;
import com.ervansh.apiautomation.core.client.ApiClient;
import com.ervansh.apiautomation.core.client.RequestSpecFactory;
import com.ervansh.apiautomation.services.products.model.CreateProductRequest;
import io.restassured.response.Response;

public final class ProductApiClient {
    private final ApiClient api;
    private final TokenManager tokenManager;
    private final RequestSpecFactory requests;

    public ProductApiClient(ApiClient api, TokenManager tokenManager) {
        this.api = api;
        this.tokenManager = tokenManager;
        this.requests = new RequestSpecFactory(api);
    }

    public Response getById(int productId) {
        return requests.json().pathParam("productId", productId).get("/products/{productId}");
    }

    public Response getAll(int limit, int skip) {
        return requests.json().queryParam("limit", limit).queryParam("skip", skip).get("/products");
    }

    public Response search(String query) {
        return requests.json().queryParam("q", query).get("/products/search");
    }

    public Response create(CreateProductRequest product) {
        return requests.json().body(product).post("/products/add");
    }

    public Response getCurrentUser() {
        return requests.authenticatedJson(tokenManager).get("/auth/me");
    }
}
