package com.ervansh.apiautomation.core.config;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

public class FrameworkConfigTest {
    @Test(groups = "configuration")
    public void normalizesBaseUriAndAcceptsPositiveTimeouts() {
        FrameworkConfig config = new FrameworkConfig("https://api.example.test///", 2_000, 5_000);

        assertEquals(config.baseUri(), "https://api.example.test");
        assertEquals(config.connectTimeoutMillis(), 2_000);
        assertEquals(config.readTimeoutMillis(), 5_000);
    }

    @Test(groups = "configuration")
    public void rejectsNonPositiveTimeouts() {
        assertThrows(IllegalArgumentException.class,
                () -> new FrameworkConfig("https://api.example.test", 0, 5_000));
    }

    @Test(groups = "configuration")
    public void loadsTheSelectedConfiguration() {
        FrameworkConfig config = FrameworkConfig.load();

        assertTrue(config.baseUri() != null && !config.baseUri().isBlank());
        assertTrue(config.connectTimeoutMillis() > 0);
        assertTrue(config.readTimeoutMillis() > 0);
    }
}
