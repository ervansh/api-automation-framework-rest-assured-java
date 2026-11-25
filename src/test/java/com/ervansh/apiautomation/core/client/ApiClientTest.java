package com.ervansh.apiautomation.core.client;

import com.ervansh.apiautomation.core.config.FrameworkConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Smoke test against the public DummyJSON product endpoint (no local server or stub). */
public class ApiClientTest {
    @Test
    public void requestSpecificationCallsPublicJsonEndpoint() {
        var response = new ApiClient(FrameworkConfig.load()).request().get("/products/1");
        assertEquals(response.statusCode(), 200);
        assertTrue(response.contentType().contains("application/json"));
        assertEquals(response.jsonPath().getInt("id"), 1);
    }
}
