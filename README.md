# Construction Contract Management System (CCM)

Modular monolith for single-company construction contract management.

## Stack

- Java 21, Spring Boot 3.5, Spring Security (JWT), Spring Data JPA, Flyway, MySQL 8.4
- Vue 3, TypeScript, Vite, Element Plus, Pinia
- MinIO (S3) for documents
- Docker Compose, JUnit 5, Mockito, Testcontainers, OpenAPI (springdoc)

## Quick start (Docker Compose)

```bash
cp .env.example .env
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- MinIO console: http://localhost:9001

Default admin (created on first boot; override with `CCM_ADMIN_PASSWORD`):

- Username: `admin`
- Password: `Admin@123`

## Local development

### Infrastructure

```bash
docker compose up -d mysql minio minio-init
```

### Backend

```bash
cd backend
export CCM_JWT_SECRET=local-dev-only-secret-key-change-in-production-32chars
export CCM_DB_PASSWORD=ccm_secret
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Vite proxies `/api` to `http://localhost:8080`.

## Tests

```bash
cd backend
mvn test
```

Requires Docker for Testcontainers (MySQL 8.4).

```bash
cd frontend
npm run build
```

## Modules

| Module | Package |
|--------|---------|
| Organization / auth | `com.company.ccm.organization` |
| Project | `com.company.ccm.project` |
| Partner | `com.company.ccm.partner` |
| Contract | `com.company.ccm.contract` |
| Approval | `com.company.ccm.approval` |
| Document | `com.company.ccm.document` |
| Change order | `com.company.ccm.changeorder` |
| Payment | `com.company.ccm.payment` |
| Settlement | `com.company.ccm.settlement` |
| Audit / reports | `com.company.ccm.audit` |

## Architecture decisions

See [docs/adr](docs/adr).

## Security notes

- Never commit real secrets; use `.env` / environment variables.
- Approved financial records are not silently mutated; use change orders / status transitions.
- Optimistic locking (`@Version`) on financial aggregates.
