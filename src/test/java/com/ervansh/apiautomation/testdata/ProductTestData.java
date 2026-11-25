package com.ervansh.apiautomation.testdata;

import com.ervansh.apiautomation.services.products.model.CreateProductRequest;
import org.testng.annotations.DataProvider;

/** Deterministic test data kept separate from API test logic. */
public final class ProductTestData {
    private ProductTestData() {}

    @DataProvider(name = "validProductIds", parallel = true)
    public static Object[][] validProductIds() {
        return new Object[][] {{1}, {2}, {3}};
    }

    @DataProvider(name = "productCreationRequests")
    public static Object[][] productCreationRequests() {
        return new Object[][] {
                {new CreateProductRequest("Automation product A", "Created by API test", "beauty", 19.99)},
                {new CreateProductRequest("Automation product B", "Created by API test", "beauty", 29.99)}
        };
    }
}
