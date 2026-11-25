# API Automation Framework (Java + REST Assured)

A Java API test automation framework built with Maven, REST Assured, and TestNG. The repository is designed for realistic service-level API testing and cross-service business workflows.

## Current implementation

- Maven project targeting Java 21.
- Immutable framework configuration loaded from environment-specific properties.
- Configuration overrides through JVM system properties and environment variables.
- Shared REST Assured request-specification factory with configurable connection/read timeouts.
- TestNG configuration checks and a local HTTP self-test for the shared request factory.

The framework is being built incrementally. Items in the architecture below are intended design boundaries; they are not claims that every capability is implemented.

## Technology baseline

| Component | Version |
|---|---|
| Java | 21 |
| REST Assured | 6.3.0 |
| TestNG | 7.9.0 |
| Maven Compiler Plugin | 3.14.1 |
| Maven Surefire Plugin | 3.6.1 |

Versions are centralized in `pom.xml`.

## Architecture and boundaries

1. **Tests** — TestNG scenarios and business assertions.
2. **Workflows** — optional orchestration for journeys spanning multiple services.
3. **Service clients** — endpoint paths, request/response models, and service-specific operations.
4. **HTTP client** — reusable REST Assured request creation and common transport settings.
5. **Core support** — configuration, authentication, test data, logging, and lifecycle support.

Tests should not use REST Assured directly. Assertions belong in tests, not in the shared HTTP client. Add interfaces only where an alternate implementation or a meaningful boundary justifies one.

## Repository layout

```text
src/
  main/java/com/ervansh/apiautomation/
    core/
      client/       # Shared REST Assured request factory
      config/       # Immutable configuration and validation
  test/
    java/           # TestNG tests, including a local HTTP self-test
    resources/
      config/       # Environment properties
```

## Prerequisites

- JDK 21
- Maven 3.9 or later

Verify the active toolchain:

```bash
java -version
mvn -version
```

## Build and run tests

```bash
mvn clean verify
```

Select an environment using `-Dtest.environment=<name>`; the matching file must exist at `src/test/resources/config/<name>.properties`.

For example:

```bash
mvn clean verify -Dtest.environment=qa
```

## Configuration

The default environment is `qa`. Current properties:

| Property | Purpose |
|---|---|
| `api.baseUri` | API base URI |
| `api.connectTimeoutMillis` | Connection timeout in milliseconds |
| `api.readTimeoutMillis` | Read/socket timeout in milliseconds |

For each property, precedence is:

1. JVM system property (for example, `-Dapi.baseUri=https://host`)
2. Environment variable (for example, `API_BASE_URI`)
3. Selected properties file

The environment selector itself uses `-Dtest.environment`, then `TEST_ENVIRONMENT`, then defaults to `qa`.

The checked-in `qa.properties` uses `https://example.invalid` as a non-routable placeholder. The current tests use an in-process HTTP server and do not call a live API. Set a real API base URI before adding/running integration tests.

## Secrets and test safety

- Never commit credentials, access tokens, or other secrets.
- Supply secrets through environment variables or a secrets manager.
- Do not disable TLS certificate validation for normal test execution.
- Avoid shared mutable request/response state between parallel tests.

## Next implementation increment

Add service-specific API clients and DTOs, then a first service-level test against a controlled test API. Cross-service workflows, reporting, and richer lifecycle support will be added as the suite grows.
