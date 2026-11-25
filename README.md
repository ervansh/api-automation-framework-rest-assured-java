# API Automation Framework — Java + REST Assured

A Java 21, Maven, REST Assured and TestNG framework using the publicly hosted **DummyJSON** API as its ecommerce/authentication test system. It sends real HTTP requests to `https://dummyjson.com`; there are no in-process HTTP stubs. DummyJSON is a test-data service: product writes are simulated and are not persisted.

## Implemented

- Environment configuration and request timeouts.
- DummyJSON authentication: login, access/refresh token storage, refresh endpoint.
- REST Assured filter: on HTTP 401, refreshes once and retries the original request once; it does not retry other statuses or loop.
- Ecommerce product list/detail/create operations.
- Java record request/response DTOs for JSON serialization and deserialization, including nested product dimensions, metadata and reviews.
- JSON Schema validation for product, product-list and login responses.
- TestNG integration tests against public DummyJSON endpoints.

## Technology baseline

| Component | Version |
|---|---:|
| Java | 21 |
| REST Assured | 6.3.0 |
| TestNG | 7.9.0 |
| Jackson Databind | 2.23.0 |
| Maven Compiler Plugin | 3.14.1 |
| Maven Surefire Plugin | 3.5.4 |

Versions are centralized in `pom.xml`.

## Structure

```text
src/main/java/com/ervansh/apiautomation/
  core/auth/       # Auth API, token pair, token manager and 401 refresh filter
  core/client/     # Shared REST Assured request factory
  core/config/     # Environment configuration
  services/products/
    model/         # Product, product-list and create-product DTOs
    ProductApiClient.java
src/test/java/com/ervansh/apiautomation/
  services/products/ # Live DummyJSON integration tests
src/test/resources/
  config/qa.properties
  schemas/         # JSON Schema contracts
.github/workflows/ci.yml
```

## Public API contract used

Base URL: `https://dummyjson.com`

| Purpose | Method | Endpoint |
|---|---|---|
| Login | POST | `/auth/login` |
| Current authenticated user | GET | `/auth/me` |
| Refresh session | POST | `/auth/refresh` |
| Product list | GET | `/products?limit=5&skip=0` |
| Product details | GET | `/products/{id}` |
| Search products | GET | `/products/search?q=phone` |
| Simulated product creation | POST | `/products/add` |

The provider documents the demo credentials `emilys` / `emilyspass`. They are public test credentials, not private secrets. For a different environment, override them rather than editing source.

## Build and run

Prerequisites: JDK 21 and Maven 3.9+.

```bash
mvn --batch-mode clean verify
```

Override configuration without changing source:

```bash
mvn clean verify -Dapi.baseUri=https://dummyjson.com
mvn clean verify -Dapi.test.username=emilys -Dapi.test.password=emilyspass
```

Supported environment overrides:

| JVM property | Environment variable | Purpose |
|---|---|---|
| `api.baseUri` | `API_BASE_URI` | API base URI |
| `api.connectTimeoutMillis` | `API_CONNECT_TIMEOUT_MILLIS` | Connect timeout |
| `api.readTimeoutMillis` | `API_READ_TIMEOUT_MILLIS` | Read timeout |
| `api.test.username` | `API_TEST_USERNAME` | Test user |
| `api.test.password` | `API_TEST_PASSWORD` | Test password |
| `api.test.tokenLifetimeMinutes` | `API_TEST_TOKEN_LIFETIME_MINUTES` | Requested access-token lifetime |

## Token lifecycle and refresh

1. `TokenManager` lazily logs in and retains both tokens in memory.
2. `TokenRefreshFilter` adds the current access token to the protected request.
3. Only an HTTP 401 triggers refresh.
4. The filter calls `/auth/refresh`, updates the token pair and retries the original request once.
5. A second 401 is returned to the test; there is no infinite retry.
6. A lock prevents concurrent requests from needlessly refreshing the same rejected token.

This is a test-framework implementation. A production token lifecycle must also follow the target identity provider's refresh-token rotation, revocation, clock-skew and concurrency rules.

## Serialization and deserialization

REST Assured/Jackson serializes `CreateProductRequest` to JSON when passed to `.body(request)`. The response is deserialized into `Product` or `ProductListResponse` using `.as(Product.class)`. The product model includes nested `Dimensions`, `Meta` and `Review` records to demonstrate nested JSON mapping.

## Schema validation

JSON Schema files live under `src/test/resources/schemas`. Tests validate response structure and types independently of Java DTO mapping. Keep schemas aligned with the provider's published response contract; do not make optional provider fields mandatory without confirming the contract.

## Important test-data limitations

DummyJSON is intended for development/testing. It returns realistic sample data, but create/update/delete operations are simulated and are not durable business transactions. The public API is external, so CI tests require internet access and can be affected by provider availability/rate limiting. These are integration tests, not isolated unit tests.

## CI and reports

GitHub Actions runs `mvn --batch-mode --no-transfer-progress clean verify`. TestNG/Surefire reports are written to `target/surefire-reports/`.


## Framework execution features

- **Reusable specifications:** `RequestSpecFactory` creates fresh JSON request specifications; `ResponseSpecFactory` provides reusable HTTP 200/201 JSON response contracts.
- **Reusable assertions:** `ApiAssertions` centralizes status, JSON content-type and response-time checks.
- **Test data:** `ProductTestData` supplies deterministic product IDs and product-creation DTOs through TestNG data providers.
- **Test organization:** tests are grouped as `configuration`, `smoke` and `regression`. The checked-in `testng.xml` suite is the Maven Surefire entry point.
- **Parallel execution:** TestNG runs independent suite tests concurrently and data-provider cases on a bounded pool. API requests use fresh REST Assured specifications; token refresh is guarded by a lock. Avoid sharing mutable test fixtures between parallel tests.
- **Lifecycle logging:** `TestExecutionListener` logs test start/end and pass/fail/skip outcomes. It deliberately does not log request bodies, credentials or bearer tokens.
- **Reports:** Maven Surefire writes machine-readable XML and text reports to `target/surefire-reports/`. TestNG also produces its standard suite report output.

Run the suite:

```bash
mvn --batch-mode clean verify
```

Select an environment profile:

```bash
mvn clean verify -Dtest.environment=qa
mvn clean verify -Dtest.environment=staging -Dapi.baseUri=https://your-staging-host
mvn clean verify -Dtest.environment=prod -Dapi.baseUri=https://your-approved-production-host
```

The staging and production profile files intentionally do not define a target URI. Supply the correct endpoint explicitly; do not run destructive or authentication test scenarios against production.

Run selected TestNG groups by overriding suite configuration or creating a dedicated suite XML. The default suite includes configuration, smoke and regression groups. The refresh-expiry test waits beyond the one-minute demo token lifetime and therefore adds about 65 seconds to a full run.
