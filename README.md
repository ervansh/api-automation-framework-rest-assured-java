# API Automation Framework — Java + REST Assured

A maintainable API automation framework foundation using Java 21, Maven, REST Assured, and TestNG. It supports service-level tests and can be extended for cross-service business workflows. The checked-in product client is an illustrative sample, not a verified production API contract.

## Implemented

- Immutable, validated environment configuration with system-property > environment-variable > properties-file precedence.
- Shared REST Assured request factory with base URI, JSON defaults, and connection/read timeouts.
- Optional bearer authentication, with token injection from a JVM property or environment variable.
- Sample product service client and product model to demonstrate the service-client boundary.
- TestNG checks for configuration, bearer auth behavior, and service-client behavior.
- In-process HTTP tests for the shared client and sample operations. They do not require a live API.
- GitHub Actions workflow running Maven verification on pushes, pull requests, and manual dispatch.

## Technology baseline

| Component | Version |
|---|---|
| Java | 21 |
| REST Assured | 6.3.0 |
| TestNG | 7.9.0 |
| Maven Compiler Plugin | 3.14.1 |
| Maven Surefire Plugin | 3.6.1 |

Versions are centralized in `pom.xml`.

## Structure

```text
src/main/java/com/ervansh/apiautomation/
  core/
    client/       # Shared request factory and bearer token provider
    config/       # Environment configuration and validation
  services/
    products/
      model/      # Product request/response model
      ProductApiClient.java

src/test/java/com/ervansh/apiautomation/
  core/           # Core configuration, client, and auth tests
  services/       # Service-client tests against local HTTP stubs
src/test/resources/config/
  qa.properties
.github/workflows/ci.yml
```

## Build and run

Prerequisites: JDK 21 and Maven 3.9 or later.

```bash
java -version
mvn -version
mvn --batch-mode clean verify
```

Select the configuration profile using `-Dtest.environment=<name>`. The matching resource must exist at `src/test/resources/config/<name>.properties`.

```bash
mvn clean verify -Dtest.environment=qa
```

## Configuration

| Property | Environment variable | Meaning |
|---|---|---|
| `api.baseUri` | `API_BASE_URI` | API base URI |
| `api.connectTimeoutMillis` | `API_CONNECT_TIMEOUT_MILLIS` | Connect timeout |
| `api.readTimeoutMillis` | `API_READ_TIMEOUT_MILLIS` | Socket/read timeout |

For each value, override precedence is JVM system property, environment variable, then selected properties file. The environment name is selected by `-Dtest.environment`, then `TEST_ENVIRONMENT`, and defaults to `qa`.

The checked-in `qa.properties` uses `https://example.invalid` as a non-routable placeholder. The local stub tests create their own loopback server and do not use that base URI. Configure a real base URI before adding integration tests for a target service.

## Authentication and secret handling

The unauthenticated `ApiClient.request()` creates a fresh request specification. Use `ApiClient.authenticatedRequest()` when the target endpoint requires bearer authentication. The token is read from `-Dapi.bearerToken` first, then `API_BEARER_TOKEN`; missing tokens fail fast. Do not commit or print credentials. For CI, inject secrets through the repository/environment secret store.

This supports bearer tokens only. Add other authentication mechanisms only when the target API requires them.

## Extending for a real API

1. Confirm the API's documented paths, payload schemas, status codes, and authentication scheme.
2. Add a service client under `services/<service>`; keep endpoint-specific request construction there.
3. Add request/response models matching the actual contract.
4. Put business assertions in TestNG tests, not in the shared HTTP client.
5. Use isolated test data and avoid shared mutable request state in parallel tests.
6. Keep TLS certificate validation enabled outside explicitly controlled local test setups.

The sample `/products` paths and fields are illustrative. Replace them with the target API contract before treating those tests as service acceptance tests.

## CI and reports

The GitHub Actions workflow runs `mvn --batch-mode --no-transfer-progress clean verify`. Maven Surefire writes TestNG results under `target/surefire-reports/` after a successful local run or CI job.

## Scope

This is a reusable framework foundation with a controlled sample service test. It does not include a target-system contract, schema validation, retry policy, external test-data management, cross-service workflows, or production service acceptance tests. Those depend on the actual API contracts and should not be guessed.
