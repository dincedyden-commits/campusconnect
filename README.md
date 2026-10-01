# CampusConnect

CampusConnect is a Spring Boot 4 + Java 21 university social platform with a static web frontend, MySQL persistence, WebSocket calling/signaling, optional Gemini AI, and optional S3-compatible media storage.

## Run locally

Requirements:
- Java 21
- MySQL/MariaDB
- Maven (or the included Maven Wrapper)

From the project root:

```cmd
mvnw.cmd clean package -DskipTests
mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080
```

The application creates/updates its database schema with Hibernate.

## Production deployment

This repository is prepared for Docker deployment on Render.

Important files:
- `Dockerfile` — production Java 21 image
- `.dockerignore` — keeps build output, local uploads and secrets out of the image
- `render.yaml` — Render web-service configuration
- `.env.example` — names of supported environment variables

### Required production environment variables

Database:
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

Optional AI:
- `GEMINI_API_KEY`
- `GEMINI_MODEL`

Recommended for permanent media:
- `STORAGE_ENABLED=true`
- `STORAGE_ENDPOINT`
- `STORAGE_BUCKET`
- `STORAGE_ACCESS_KEY`
- `STORAGE_SECRET_KEY`
- `STORAGE_REGION`
- `STORAGE_PUBLIC_BASE_URL`

System accounts:
- `SYSTEM_ACCOUNTS_ENABLED=true`
- `SYSTEM_OFFICIAL_PASSWORD`
- `SYSTEM_ADMIN_PASSWORD`

Do not commit real passwords, API keys, database URLs, `.env` files, or object-storage credentials.

### Media warning

If `STORAGE_ENABLED=false`, uploaded files are written to the container's `uploads/` directory. Container-local files are not a reliable production media store because they can disappear when the service is redeployed. Use S3-compatible storage such as Cloudflare R2 or AWS S3 for permanent media.

### Password reset warning

The current password-reset flow is designed for development and does not send email. Before treating the app as a production authentication system, connect it to a real email/OTP provider and add rate limiting.

## Render

Create the web service from the Git repository and select the Docker runtime. Render will use the repository's `Dockerfile`.

The application listens on `${PORT}` when Render provides it, and falls back to port `8080` locally.

Health check:

```text
/api/health
```

For MySQL, use a persistent MySQL service/database and provide its host, database name, username and password to the CampusConnect web service.

## No fake seed content

The application does not insert fake users, posts, listings or conversations. System accounts and official communities are created only when the corresponding system-account setting is enabled.
