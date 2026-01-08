# PLAN: AI Task Management App (Spring Boot 4)

## 1) Ziele & Scope
- Kern: Task-Management mit AI-Unterstützung (Priorisierung, Zerlegung, Deadlines, Smart Notifications)
- UI: Responsive Web-UI (Thymeleaf + HTMX + Bootstrap), Kanban & Detail-Views
- GitHub: Quelle für Issues/Bugs/Features, Releases als Versionierung, PR-Flow
- Qualität: CI/CD, Tests, Security, Observability
- Nicht-Ziele (v1): Mobile App, Offline-First, Multi-Tenancy, SSO

## 2) Architektur-Überblick
- Backend: Spring Boot 4 (Java 21), Virtual Threads wo sinnvoll, Spring Web, Data JPA
- UI: Thymeleaf Server-Side Rendering + HTMX für dynamische Fragmente, Bootstrap 5 für Responsive
- Datenbank: Dev/Test H2, Prod PostgreSQL
- AI: LangChain4j + OpenAI (abstraktiert via Service-Interface); Agents als eigenständige Services
- Security: JWT, RBAC, Validation, Rate Limiting, Security Headers
- Realtime: WebSocket für Task-Updates/Notifications
- Observability: Actuator, Micrometer + Prometheus, strukturierte Logs

## 3) Domain & Datenmodell (vereinfacht)
- Task(id, title, description, priority, status, dueDate, tags, assignee, parentTaskId, createdAt, updatedAt)
- Subtask(id, taskId, title, status)
- Comment(id, taskId, authorId, text, createdAt)
- User(id, username, email, passwordHash, role)
- AIAnalysis(id, taskId, agentType, resultJson, confidence, createdAt)

## 4) AI Agents (Inputs/Outputs/Trigger)
- PrioritizationAgent: Input(title, description, dueDate, tags); Output(priority, rationale); Trigger: Create/Update
- DecompositionAgent: Input(title, description); Output(List<subtasks>); Trigger: On demand + Create for complex tasks
- DeadlinePredictionAgent: Input(title, description, history?); Output(dueDate suggestion, rationale); Trigger: Create/Update
- SmartNotificationAgent: Input(task state changes, due dates); Output(reminders via WebSocket/Email hook); Trigger: Schedule + events
- Eval: Log confidence, capture feedback (accept/reject), add traceId for observability

## 5) API & UI (high level)
- REST Endpoints: /api/tasks (CRUD, search, pagination), /api/ai/* (prioritize, decompose, deadline, suggestions/{taskId}), /api/auth/* (login/register/refresh), /api/users/me, WebSocket /ws/tasks
- UI: Dashboard (list + filters), Kanban, Task-Detail (AI panel, comments, activity), Responsive layout

## 6) GitHub als Git-Server & Projektsteuerung
- Issues: Templates (Bug, Feature, AI Agent Enhancement), Labels (bug, feature, ai, ui, backend, security, help wanted), Milestones nach Releases
- PR-Flow: Branching (main, develop, feature/*, bugfix/*, release/*), 1 Review Pflicht, Checks grün, Conventional Commits
- Releases: GitHub Releases automatisiert aus CI (Changelog generieren), Tags vX.Y.Z, Release Assets (JAR/Docker image link)
- Security: Dependabot (Maven, GitHub Actions), CodeQL, secret scanning

## 7) CI/CD Pipeline (GitHub Actions)
- Jobs: lint/test (mvn -B test), build (mvn -B package), security (OWASP/Dependency-Check or Trivy), docker-build (if needed), release (tag + GitHub Release), deploy (staging/prod optional)
- Gates: Tests grün, min. Coverage 80% (Jacoco), static analysis (SpotBugs/PMD optional), license check

## 8) Environment & Config
- Profiles: dev (H2, debug logging), prod (PostgreSQL, SSL, caching optional)
- Required env vars: OPENAI_API_KEY, SPRING_DATASOURCE_URL/USERNAME/PASSWORD, JWT_SECRET
- application-dev.yml: H2 in-memory, ddl-auto=create-drop, show-sql=true, langchain4j.open-ai.model-name=gpt-4-turbo
- application-prod.yml: ddl-auto=validate, show-sql=false, SSL on, connection pool tuned

## 9) Security & Compliance
- AuthN: JWT short-lived, refresh tokens
- AuthZ: RBAC (roles: ADMIN, USER), task ownership checks
- API Hardening: Input validation, size limits, rate limiting, CORS, security headers
- Data Protection: BCrypt passwords, no secrets in repo, HTTPS required, PII minimal
- AI Safety: Rate limits to OpenAI, retry with backoff, circuit breaker, redact sensitive fields in prompts

## 10) Testing-Strategie
- Unit: Services, Agents (LLM mocked), Repositories (DataJpaTest)
- Integration: REST (MockMvc), Security, DB (Testcontainers Postgres), WebSocket
- E2E: Minimal happy-path UI (Selenium/Playwright) optional; Performance: JMeter/ghz optional
- Coverage: Ziel ≥80%

## 11) Deployment
- Dev: mvnw spring-boot:run, H2
- Staging: Docker image + PostgreSQL, run via docker-compose; env via secrets
- Prod: Docker/K8s, PostgreSQL managed, Redis optional for cache; HTTPS (LetsEncrypt/Ingress)
- Backups: Daily DB backups, retention 30d; RTO<1h, RPO<24h

## 12) Observability
- Actuator: health, metrics, info, prometheus
- Logging: JSON, correlation-id, log level per profile
- Metrics: p95 latency (API<200ms, AI<3s), throughput, error rate, DB pool metrics
- Alerts: high error rate, slow responses, AI failures, DB connection issues

## 13) Roadmap (Woche)
- W1-2 Foundation: Project skeleton, domain model, CRUD, H2, basic tests
- W3-4 Core API: Auth (JWT), PostgreSQL, pagination/filtering, validation/errors
- W5-6 AI: LangChain4j setup, prioritization, decomposition, deadline agent, feedback loop
- W7-8 UI: Thymeleaf+HTMX views, dashboard, task detail, AI panel, responsive
- W9-10 Realtime & Notifications: WebSocket, notification agent, search, file upload (optional)
- W11-12 Quality/DevOps: Hardening, perf, security scan, CI/CD, monitoring
- W13 Release: Prod deploy, UAT, fixes, release v1.0.0

## 14) Risks & Mitigation
- AI cost/latency: cache prompts/results, cap tokens, batch where possible
- Data quality: validation, required fields, sane defaults
- Security: strict validation, rate limiting, regular deps updates, secrets in env
- Scope creep: roadmap locks scope; later features in v2 bucket (mobile, SSO, advanced analytics)

## 15) Open Questions
- Need staging/prod targets defined (cloud/vendor)?
- Email provider for notifications?
- UI branding/theme requirements?
- Any compliance needs (GDPR/ISO)?

