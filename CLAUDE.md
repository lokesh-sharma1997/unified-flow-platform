# UFP — Unified Flow Platform
## Project Context for Claude Code

You are building the Unified Flow Platform (UFP) with the user.
This is a solo build (1 engineer + Claude Code). Ubuntu machine, fresh setup.
Session work happens in ~/ufp/. Push to GitHub after every session.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Angular 17+ with Module Federation (micro frontends) |
| Backend Core | Java 21 + Spring Boot 3.4 |
| AI/ML/Agents | Python 3.12 + FastAPI |
| High-perf services | Go 1.22 |
| Messaging | Apache Kafka |
| Database | PostgreSQL 16 |
| Cache | Redis 7 |
| Object store | MinIO |
| Search | OpenSearch |
| Observability | Prometheus + Grafana + Loki + Tempo |
| Source of truth | GitHub (all definitions in YAML) |

---

## Monorepo Structure

ufp/
├── CLAUDE.md
├── README.md
├── frontend/
│   ├── shell-mfe/                     (Angular host, port 4200)
│   ├── designer-mfe/                  (Visual DAG editor, port 4201)
│   ├── execution-mfe/                 (Run monitoring, port 4202)
│   ├── human-task-mfe/                (Approvals inbox, port 4203)
│   ├── governance-mfe/                (Data catalog + lineage, port 4204)
│   └── agent-builder-mfe/             (AI agent graph editor, port 4205)
├── backend/
│   ├── java/
│   │   ├── api-gateway-service/       (port 8080)
│   │   ├── iam-rbac-service/          (port 8081)
│   │   ├── workspace-service/         (port 8082)
│   │   ├── definition-service/        (port 8083)
│   │   ├── gitops-service/            (port 8084)
│   │   ├── workflow-engine-service/   (port 8085, CORE)
│   │   ├── human-task-service/        (port 8086)
│   │   ├── catalog-service/           (port 8087)
│   │   ├── metadata-catalog-service/  (port 8088)
│   │   ├── lineage-service/           (port 8089)
│   │   ├── observability-service/     (port 8090)
│   │   └── audit-service/             (port 8091)
│   ├── python/
│   │   ├── agent-runtime-service/     (port 8200)
│   │   ├── experiment-service/        (port 8201)
│   │   ├── feature-store-service/     (port 8202)
│   │   ├── model-monitoring-service/  (port 8203)
│   │   ├── notebook-manager-service/  (port 8204)
│   │   └── rag-service/               (port 8205)
│   └── go/
│       └── mcp-gateway/               (port 8300)
├── infra/
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   └── schema.sql
│   ├── k8s/
│   │   ├── base/
│   │   └── overlays/
│   └── scripts/
└── gitops-repo/
    ├── workflows/
    ├── pipelines/
    ├── mlops/
    └── environments/

---

## Build Phases

### Phase 1 — Foundation (CURRENT)

- Session 1: Install toolchain DONE
- Session 2: GitHub repo init + full monorepo scaffold
- Session 3: Docker Compose — Postgres, Redis, Kafka, MinIO, OpenSearch
- Session 4: Kafka topics + PostgreSQL schema + Flyway migrations
- Session 5: iam-rbac-service (Spring Boot, JWT, users, roles)
- Session 6: iam-rbac-service (RBAC, permissions, tests)
- Session 7: api-gateway-service (Spring Cloud Gateway, JWT filter, routing)
- Session 8: workspace-service (workspace + project CRUD)
- Session 9: shell-mfe (Angular init, Module Federation, routing)
- Session 10: shell-mfe login UI + auth guard + HTTP interceptor
- Session 11: workspace-mfe (list + create, connected to API)
- Session 12: Prometheus + Grafana + Loki + Tempo wired to services
- Session 13: GitHub Actions CI/CD (build, test, Docker image push)
- Session 14: Deploy to local k8s (Kustomize), smoke test, tag v0.0.1

### Phase 2 — Workflow Engine + Designer
### Phase 3 — Human Tasks + Governance
### Phase 4 — Streaming + Spark + Notebooks
### Phase 5 — MLOps Lifecycle
### Phase 6 — AI Agent Builder
### Phase 7 — Hardening + GA

---

## Core Principles (never violate)

1. GitHub = source of truth. All flow/pipeline/agent definitions are YAML files in git.
2. DB = metadata + execution state only. Never store definitions in DB.
3. Event-driven. All state changes emit Kafka events.
4. Every session ends with something runnable.
5. Commit after every session with a meaningful message.

---

## Flow Types Supported

WORKFLOW, CYCLIC_WORKFLOW, STREAMING_PIPELINE, BATCH_TASK, SPARK_JOB,
NOTEBOOK_TASK, ML_TRAINING_JOB, MODEL_DEPLOYMENT, BATCH_INFERENCE,
REALTIME_INFERENCE, HUMAN_TASK, AI_AGENT_FLOW, RUNBOOK_FLOW, GOVERNANCE_CHECK

---

## Session Rules for Claude Code

When starting any session:
1. Read this file first
2. Run git log --oneline -5 to see what was completed
3. State clearly what this session will deliver
4. End every session with: git add . && git commit -m "Phase 1 SessionN: description"

## Installed Tools (Session 1 complete)
- Java 21 (Temurin)
- Maven 3.x
- Node 20 + npm
- Angular CLI 17
- Docker Engine
- Go 1.22
- GitHub CLI (gh)
- Git
