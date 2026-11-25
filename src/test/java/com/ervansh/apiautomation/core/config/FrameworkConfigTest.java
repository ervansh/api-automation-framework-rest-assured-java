package com.ervansh.apiautomation.core.config;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class FrameworkConfigTest {
    @Test
    public void loadsQaDefaultsAndNormalizesBaseUri() {
        FrameworkConfig config = FrameworkConfig.load();

        assertEquals(config.baseUri(), "https://example.invalid");
        assertTrue(config.connectTimeoutMillis() > 0);
        assertTrue(config.readTimeoutMillis() > 0);
    }
}
