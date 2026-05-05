# Unified Flow Platform (UFP)

Enterprise platform for workflows, streaming pipelines, MLOps, AI agents, and data governance.

## Services

| Service | Port | Description |
|---------|------|-------------|
| ufp-gateway | 8080 | API Gateway (Spring Cloud Gateway) |
| ufp-workflow | 8081 | Workflow engine (DAG execution) |
| ufp-pipeline | 8082 | Streaming pipeline management |
| ufp-mlops | 8083 | Model registry & deployment |
| ufp-agent | 8084 | AI agent runtime |
| ufp-governance | 8085 | Data governance & lineage |

## Frontend

| App | Port | Description |
|-----|------|-------------|
| ufp-portal | 4200 | Angular web application |

## Infrastructure (local)

| Component | Port | Description |
|-----------|------|-------------|
| PostgreSQL | 5432 | Primary database |
| Kafka | 9092 | Event streaming |
| Kafka UI | 9093 | Kafka management UI |
| Redis | 6379 | Cache & session store |
| MinIO | 9000/9001 | Object storage (models, artifacts) |

## Quick Start

```bash
# Start local infrastructure
docker compose up -d

# Build all Java services
mvn clean install -DskipTests

# Run a service (e.g. workflow)
cd services/ufp-workflow && mvn spring-boot:run

# Run Angular portal
cd frontend/ufp-portal && ng serve

# Build Go stream processor
cd go-services/ufp-stream-processor && go build ./...
```

## Stack

- **Java 21** + Spring Boot 3.3 + Spring Cloud
- **Angular 21** (standalone components, SCSS)
- **Go 1.23** (stream processor)
- **PostgreSQL 16**, **Kafka 7.6**, **Redis 7**, **MinIO**
- **Docker Compose** (local dev), **Kubernetes** (production)
