# API Automation Framework (Java + REST Assured)

A maintainable API test automation framework built with Java, REST Assured, TestNG, and Maven. The repository is intended for practical learning and for demonstrating production-oriented API automation design.

## Goals

- Keep HTTP mechanics reusable and business/service operations readable.
- Organize tests by service while supporting cross-service workflows.
- Support environment-specific configuration without hard-coded URLs or credentials.
- Make test data isolated and suitable for parallel execution.
- Produce useful test reports while avoiding accidental exposure of secrets.

## Planned technology baseline

- Java 21
- Maven
- REST Assured
- TestNG
- Jackson for JSON mapping
- SLF4J with Logback for logging
- Allure for test reporting

Dependency versions will be managed centrally in the Maven build and documented as implementation is added.

## Architecture

The framework is organized into these responsibilities:

1. **Tests** — TestNG scenarios and assertions.
2. **Workflows** — business journeys that coordinate multiple services when needed.
3. **Service clients** — service-specific endpoints and request/response models.
4. **HTTP client** — shared REST Assured request execution and common specifications.
5. **Core support** — configuration, authentication, test data, logging, and lifecycle support.

Tests should not use REST Assured directly. Assertions belong in tests, not in the shared HTTP client.

## Repository status

The framework is being implemented incrementally. This README will be updated as components become available. Do not assume a feature is implemented merely because it appears in the architecture goals.

## Prerequisites

- JDK 21
- Maven 3.9 or later
- Git

## Build and test

Once the Maven project is added:

```bash
mvn clean verify
```

Environment-specific execution and configuration instructions will be documented here when the configuration files and test suite are in place.

## Configuration and secrets

Environment URLs and non-sensitive settings belong in configuration files. Credentials, tokens, and other secrets must be supplied through environment variables or a secrets manager; do not commit them to the repository.

## Contribution notes

- Keep service-specific behavior in service clients or workflows, not in the generic HTTP layer.
- Avoid shared mutable state between tests.
- Add or update tests and documentation with each framework capability.
