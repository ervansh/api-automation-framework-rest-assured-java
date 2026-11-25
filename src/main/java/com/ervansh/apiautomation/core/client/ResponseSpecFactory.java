package com.ervansh.apiautomation.core.client;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

/** Common response contracts; endpoint-specific assertions remain in tests. */
public final class ResponseSpecFactory {
    private ResponseSpecFactory() {}

    public static ResponseSpecification jsonOk() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType("application/json")
                .build();
    }

    public static ResponseSpecification jsonCreated() {
        return new ResponseSpecBuilder()
                .expectStatusCode(201)
                .expectContentType("application/json")
                .build();
    }
}
