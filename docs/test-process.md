# Test Process — HumanizerDemo Java CI/CD Project

## 1. Overview

Defines the **who, when, and how** of running tests for the HumanizerDemo
Java project. Covers unit tests, integration tests, the change-gated
optimisation strategy, and result storage locations.

---

## 2. Test Techniques in Use

| Technique | Tool | Purpose |
|-----------|------|---------|
| Unit Testing | JUnit 5 + AssertJ | Verify each service method in isolation |
| Integration Testing | JUnit 5 + ServiceRegistry | Verify services compose correctly end-to-end |
| Parameterised Testing | JUnit 5 `@ParameterizedTest` | Data-driven assertions across input variants |
| Soft Assertions | AssertJ `SoftAssertions` | Collect all failures in one pass |
| Test Suite Optimisation | Jenkins `changeset` condition | Skip integration tests on docs/config-only commits |
| Code Coverage | JaCoCo + HTML report | Measure adequacy per build |

---

## 3. Java Version Compatibility

The `pom.xml` compiles with `source=8` and `target=8`, making the bytecode
runnable on both:

- `java-21-amazon-corretto` (EC2 Amazon Linux 2023)
- `OpenJDK Temurin 1.8.0_482` (legacy environments)

No Java 9+ module system or records are used in any source file.

---

## 4. Who Runs the Tests

| Role | Responsibility |
|------|---------------|
| Developer | Runs unit tests locally before every commit (`./mvnw test -P unit-tests`) |
| Jenkins EC2 agent | Runs all pipeline stages automatically on every push or PR |
| Tech Lead / Reviewer | Reviews JaCoCo coverage HTML and JUnit trend before merging a PR |
| QA Engineer (optional) | Periodically reviews coverage gaps; extends parameterised test cases |

---

## 5. When Tests Run

```
Commit pushed / PR opened
        │
        ▼
┌─────────────────────────────────────────────┐
│  Stage: Build                               │
│  mvnw clean compile — always               │
└─────────────────────┬───────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────┐
│  Stage: Unit Tests                          │
│  ALWAYS — every commit, every branch        │
│  mvnw test -P unit-tests  (~3-5 s)          │
└─────────────────────┬───────────────────────┘
                      │
           ┌──────────┴──────────┐
           │                     │
    src/main, src/test      docs/, *.md,
    or pom.xml changed      Jenkinsfile only
           │                     │
           ▼                     ▼
┌──────────────────┐   ┌─────────────────────┐
│ Integration Tests│   │  SKIPPED (optimised)│
│ -P integration-  │   └─────────────────────┘
│ tests (~8-12 s)  │
└──────────┬───────┘
           │
           ▼
┌─────────────────────────────────────────────┐
│  Stage: Coverage Report                     │
│  mvnw verify -P all-tests + jacoco:report   │
│  Published to Jenkins sidebar               │
└─────────────────────────────────────────────┘
```

### Branch policy

| Branch | Unit Tests | Integration Tests |
|--------|-----------|-------------------|
| `feature/*` | Always | If src or pom changed |
| `develop` | Always | Always |
| `main` / `master` | Always | Always |

---

## 6. How to Run Tests

### 6.1 Locally

```bash
./mvnw clean test -P unit-tests

./mvnw clean test -P integration-tests

./mvnw clean verify -P all-tests

open target/coverage-report/index.html
```

### 6.2 Run a specific test class

```bash
./mvnw test -P unit-tests -Dtest=TextProcessorTests

./mvnw test -P integration-tests -Dtest=ServiceCompositionTests
```

### 6.3 Via Jenkins (automated)

1. GitHub webhook fires on push/PR → Jenkins receives event
2. Build → Unit Tests → Integration Tests (if gated) → Coverage → Archive
3. JUnit XML results published to Jenkins test trend chart
4. JaCoCo HTML report published to Jenkins sidebar
5. GitHub commit status updated to pass/fail

---

## 7. Where Results Are Stored

| Artefact | Location | Retention |
|----------|----------|-----------|
| JUnit XML results | `target/surefire-reports/*.xml` | Last 10 builds (Jenkins) |
| JaCoCo HTML report | `target/coverage-report/index.html` | Last 10 builds (Jenkins sidebar) |
| Compiled JAR | `target/*.jar` | Last 10 builds |
| Build console log | Jenkins build page | Last 10 builds |
| Test trend graph | Jenkins built-in JUnit chart | Indefinite |

Workspace is cleaned after every build (`cleanWs` in Jenkinsfile `post.always`).

---

## 8. Test Suite Optimisation Strategy

Integration tests are gated by Jenkins `changeset`:

```groovy
when {
    anyOf {
        branch 'main'
        changeset 'src/main/**'
        changeset 'src/test/**'
        changeset 'pom.xml'
    }
}
```

A commit touching only `docs/`, `README.md`, or `Jenkinsfile` skips the
integration stage, saving 8-12 seconds per build and reducing noise.

**Drawback:** If a new config file outside `src/` or `pom.xml` can affect
runtime behaviour, the `changeset` list must be extended to include it.

---

## 9. Definition of Done

A feature branch is ready to merge when:

1. All unit tests pass (`-P unit-tests`)
2. All integration tests pass (`-P integration-tests`) when triggered
3. JaCoCo line coverage on `com.humanizerdemo.service` is 75% or above
4. Zero compiler warnings at `-Xlint:all`
5. Jenkins reports a green commit status on GitHub
