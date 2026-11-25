package com.ervansh.apiautomation.services.products;

import com.ervansh.apiautomation.core.auth.AuthApiClient;
import com.ervansh.apiautomation.core.auth.TokenManager;
import com.ervansh.apiautomation.core.client.ApiClient;
import com.ervansh.apiautomation.core.config.FrameworkConfig;
import com.ervansh.apiautomation.services.products.model.CreateProductRequest;
import com.ervansh.apiautomation.services.products.model.Product;
import com.ervansh.apiautomation.services.products.model.ProductListResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.*;

public class ProductApiClientTest {
    private ProductApiClient products;
    private AuthApiClient auth;
    private TokenManager tokens;

    @BeforeClass
    public void setUp() {
        FrameworkConfig config = FrameworkConfig.load();
        ApiClient api = new ApiClient(config);
        auth = new AuthApiClient(api);
        tokens = new TokenManager(auth,
                System.getProperty("api.test.username", System.getenv().getOrDefault("API_TEST_USERNAME", "emilys")),
                System.getProperty("api.test.password", System.getenv().getOrDefault("API_TEST_PASSWORD", "emilyspass")),
                Integer.parseInt(System.getProperty("api.test.tokenLifetimeMinutes",
                        System.getenv().getOrDefault("API_TEST_TOKEN_LIFETIME_MINUTES", "1"))));
        products = new ProductApiClient(api, tokens);
    }

    @Test
    public void productListMatchesJsonSchemaAndDeserializes() {
        var response = products.getAll(5, 0);
        response.then().statusCode(200).body(matchesJsonSchemaInClasspath("schemas/product-list.schema.json"));
        ProductListResponse result = response.as(ProductListResponse.class);
        assertEquals(result.products().size(), 5);
        assertTrue(result.total() >= result.products().size());
        assertNotNull(result.products().getFirst().title());
    }

    @Test
    public void productDetailsMatchSchemaAndDeserializeNestedObjects() {
        var response = products.getById(1);
        response.then().statusCode(200).body(matchesJsonSchemaInClasspath("schemas/product.schema.json"));
        Product product = response.as(Product.class);
        assertEquals(product.id(), 1);
        assertNotNull(product.dimensions());
        assertNotNull(product.meta());
        assertNotNull(product.images());
    }

    @Test
    public void productCreateSerializesRequestAndDeserializesResponse() {
        CreateProductRequest request = new CreateProductRequest(
                "Framework test product", "Created through REST Assured", "beauty", 19.99);
        var response = products.create(request);
        response.then().statusCode(201);
        assertEquals(response.jsonPath().getString("title"), request.title());
        assertEquals(response.jsonPath().getDouble("price"), request.price());
        // DummyJSON simulates this write; it does not persist the product.
    }

    @Test
    public void loginAndCurrentUserUseManagedBearerToken() {
        var login = auth.login("emilys", "emilyspass", 1);
        login.then().statusCode(200).body(matchesJsonSchemaInClasspath("schemas/auth-login.schema.json"));
        assertNotNull(tokens.accessToken());
        var me = products.getCurrentUser();
        me.then().statusCode(200);
        assertEquals(me.jsonPath().getString("username"), "emilys");
    }

    @Test(dependsOnMethods = "loginAndCurrentUserUseManagedBearerToken")
    public void refreshEndpointRotatesTokenPair() {
        String oldAccessToken = tokens.accessToken();
        String newAccessToken = tokens.refreshAccessToken(oldAccessToken);
        assertNotNull(newAccessToken);
        assertNotEquals(newAccessToken, oldAccessToken);
        auth.currentUser(newAccessToken).then().statusCode(200);
    }
}
