package com.ervansh.apiautomation.assertions;

import io.restassured.response.Response;
import org.testng.Assert;

/** Small reusable assertions with useful failure context and no sensitive payload logging. */
public final class ApiAssertions {
    private ApiAssertions() {}

    public static void assertStatus(Response response, int expectedStatus) {
        Assert.assertEquals(response.statusCode(), expectedStatus,
                "Unexpected HTTP status. Response content type: " + response.contentType());
    }

    public static void assertJsonContentType(Response response) {
        Assert.assertTrue(response.contentType() != null
                        && response.contentType().toLowerCase().contains("application/json"),
                "Expected application/json but received: " + response.contentType());
    }

    public static void assertResponseTimeBelow(Response response, long maxMillis) {
        Assert.assertTrue(response.time() < maxMillis,
                "Response time " + response.time() + "ms exceeded " + maxMillis + "ms");
    }
}
