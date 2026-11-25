package com.ervansh.apiautomation.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Immutable, validated configuration for one test execution.
 *
 * <p>Precedence: JVM system property, environment variable, then selected
 * environment properties file. Environment variable names use the API_ prefix.</p>
 */
public record FrameworkConfig(String baseUri, int connectTimeoutMillis, int readTimeoutMillis) {
    private static final String DEFAULT_ENVIRONMENT = "qa";
    private static final String CONFIG_DIRECTORY = "/config/";

    public FrameworkConfig {
        if (baseUri == null || baseUri.isBlank()) {
            throw new IllegalArgumentException("api.baseUri must be configured");
        }
        java.net.URI parsedUri;
        try {
            parsedUri = java.net.URI.create(baseUri.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("api.baseUri must be a valid absolute HTTP(S) URI", exception);
        }
        if (!parsedUri.isAbsolute()
                || !("http".equalsIgnoreCase(parsedUri.getScheme())
                || "https".equalsIgnoreCase(parsedUri.getScheme()))
                || parsedUri.getHost() == null) {
            throw new IllegalArgumentException("api.baseUri must be a valid absolute HTTP(S) URI");
        }
        if (connectTimeoutMillis <= 0 || readTimeoutMillis <= 0) {
            throw new IllegalArgumentException("API timeouts must be positive");
        }
        baseUri = baseUri.trim().replaceAll("/+$", "");
    }

    public static FrameworkConfig load() {
        String environment = firstNonBlank(
                System.getProperty("test.environment"),
                System.getenv("TEST_ENVIRONMENT"),
                DEFAULT_ENVIRONMENT);
        if (!environment.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid test.environment profile name");
        }

        Properties properties = new Properties();
        String resource = CONFIG_DIRECTORY + environment + ".properties";
        try (InputStream input = FrameworkConfig.class.getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalStateException("Configuration resource not found: " + resource);
            }
            properties.load(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load configuration: " + resource, exception);
        }

        String baseUri = value(properties, "api.baseUri", "API_BASE_URI");
        int connectTimeout = positiveInt(value(properties, "api.connectTimeoutMillis", "API_CONNECT_TIMEOUT_MILLIS"),
                "api.connectTimeoutMillis");
        int readTimeout = positiveInt(value(properties, "api.readTimeoutMillis", "API_READ_TIMEOUT_MILLIS"),
                "api.readTimeoutMillis");

        return new FrameworkConfig(baseUri, connectTimeout, readTimeout);
    }

    private static String value(Properties properties, String key, String environmentKey) {
        return firstNonBlank(System.getProperty(key), System.getenv(environmentKey), properties.getProperty(key));
    }

    private static int positiveInt(String value, String key) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new NumberFormatException("value must be positive");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException("Invalid positive integer for " + key + ": " + value, exception);
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
