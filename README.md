# 💰 Wallet Service

A wallet microservice for managing user funds, supporting deposits, withdrawals, transfers, and historical balance
queries.  
Built with **Java 21, Spring Boot, Hexagonal Architecture, DDD, and Observability-first principles**.

---

## 📑 Table of Contents

1. [Features](#-features)
2. [Architecture & Design Choices](#-architecture--design-choices)
3. [Prerequisites](#-prerequisites)
4. [Running the Project](#️-running-the-project)
    - [Local Environment](#local-environment-h2)
    - [Development Environment](#development-environment-dockerized)
    - [Service URLs](#-service-urls)
5. [API Documentation](#-api-documentation)
6. [Security](#-security)
7. [Observability](#-observability)
8. [Assumptions & Trade-offs](#-assumptions--trade-offs)
9. [Time Tracking](#️-time-tracking)

---

## 🚀 Features

- Create wallets for users
- Deposit, withdraw, and transfer funds
- Retrieve current balance
- Retrieve historical balance at a given timestamp
- Full auditing & traceability (logs, trace IDs, events)
- Security with token-based authentication (mocked user validation)

---

## 🏗️ Architecture & Design Choices

- **Hexagonal Architecture (Ports & Adapters)** → separates domain, application, and infrastructure concerns.
- **Domain-Driven Design (DDD)** → explicit modeling of entities, aggregates, and repositories.
- **Spring Boot & Spring Security** → RESTful API with token-based authentication.
- **JPA/Hibernate** → persistence with H2 (local) and Postgres (dev).
- **Observability-first design**:
    - Structured logging with Logback + Logstash
    - Distributed tracing via OpenTelemetry + Jaeger
    - Metrics & health checks with Spring Actuator
    - Monitoring dashboards with ELK + Kibana

#### Functional Requirements

- **Create / Deposit / Withdraw / Transfer funds** → implemented as REST endpoints in the application layer, with domain
  services ensuring business rules and consistency.
- **Retrieve balance (current & historical)** → persistence layer stores transactions with timestamps, enabling current
  and historical queries.
- **Traceability** → every transaction is persisted and logged with a trace ID for full auditing.

#### Non-Functional Requirements

- **High availability & mission critical** → containerized setup with Docker Compose allows easy scaling and
  production-like testing. Spring Boot provides health checks and readiness probes via Actuator.
- **Traceability & Auditing** → implemented using structured logs, unique trace IDs (correlation IDs), and OpenTelemetry
  integration with Jaeger and ELK for visualization.
- **Security** → enforced via Spring Security with token-based authentication (simplified for this assignment, but
  easily extendable).
- **Scalability & Extensibility** → Hexagonal Architecture + DDD isolate the domain logic from infrastructure concerns,
  making it easier to add new features, change persistence, or integrate with external systems without impacting the
  core domain.
- **Monitoring & Observability** → Actuator for metrics, OpenTelemetry for traces, ELK + Kibana for log aggregation.

---

## 📋 Prerequisites

Before running the project, ensure you have installed:

- [JDK 21](https://adoptium.net/)
- [Gradle 9](https://gradle.org/install/)
- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/)

---

## ⚙️ Running the Project

### Local Environment (H2)

For a lightweight dev setup:

```bash
./gradlew bootRun
```

Services available:

- Swagger UI → [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Actuator → [http://localhost:8080/actuator](http://localhost:8080/actuator)

---

### Development Environment (Dockerized)

For a setup closer to production:

```bash
docker-compose up
```

This will start:

- **Wallet Service**
- **Postgres**
- **RabbitMQ**
- **Logstash + ELK + Kibana**
- **Jaeger**

---

### 🔗 Service URLs

After starting with Docker, the following services are available:

- Wallet API Swagger → [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Actuator → [http://localhost:8080/actuator](http://localhost:8080/actuator)
- Kibana → [http://localhost:5601](http://localhost:5601)
- Jaeger → [http://localhost:16686](http://localhost:16686)
- RabbitMQ Management → [http://localhost:15672](http://localhost:15672)

---

## 📡 API Documentation

Swagger UI exposes interactive API documentation at `/swagger-ui.html`.

Example requests:

```bash
# Create a wallet
curl -X POST http://localhost:8080/wallets      -H "Authorization: Bearer <token>"

# Deposit funds
curl -X POST http://localhost:8080/wallets/{id}/deposit      -H "Content-Type: application/json"      -d '{"amount":100.00}'

# Get balance
curl http://localhost:8080/wallets/{id}/balance
```

---

## 🔐 Security

- Token-based authentication with Spring Security.
- **User validation is mocked** (no external user system or DB).
- Assumption: In production, the service would integrate with an Identity Provider (e.g., Keycloak, Auth0).

---

## 📊 Observability

- **Logs** → structured with trace IDs for auditing.
- **Tracing** → OpenTelemetry integrated with Jaeger.
- **Metrics** → via Spring Actuator.
- **Monitoring** → ELK stack (Logstash + Elasticsearch + Kibana).

---

## 📝 Assumptions & Trade-offs

- Authentication is mocked due to scope (no external user DB).
- Transactions limited to service DB (no distributed transactions).
- Balances stored using `BigDecimal` for precision.
- No caching layer implemented (simplified for assignment).

---

## ⏱️ Time Tracking

Estimated time spent: **~10 hours**

- 8h → Core domain + API development
- 2h → Observability & infrastructure setup
- 2h → Documentation & polish

---
