# TaskFlow API

A Spring Boot REST API for managing projects and tasks, with JWT-based authentication.

## Stack

- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL + Flyway migrations
- JJWT for JSON Web Tokens
- springdoc-openapi (Swagger UI)

## Running locally

### With Docker Compose (recommended)

```bash
cd docker
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Manually

1. Start a PostgreSQL instance and create a `taskflow` database.
2. Copy `.env.example` to `.env` and adjust values, or export the variables directly.
3. Run:

```bash
mvn spring-boot:run
```

Flyway will apply migrations automatically on startup.

## API docs

Once running, Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Authentication

- `POST /api/auth/register` — create an account, returns an access token + refresh token.
- `POST /api/auth/login` — authenticate, returns an access token + refresh token.
- `POST /api/auth/refresh` — exchange a refresh token for a new access/refresh token pair (rotates and revokes the old one).
- `POST /api/auth/logout` — revoke a refresh token.
- `POST /api/auth/forgot-password` — request a password reset token. No email provider is wired up yet, so the token is written to the application log instead of being emailed.
- `POST /api/auth/reset-password` — reset a password using a token from `forgot-password`.

Send the access token on subsequent requests as `Authorization: Bearer <token>`. Auth endpoints are rate-limited to 10 requests/minute per IP.

## Authorization

- Projects and tasks are owned by the user who created them; only the owner (or a user with the `ADMIN` role) can update/delete them.
- `GET /api/users/{id}` is restricted to the user themself or an admin.
- `GET /api/users` (paginated) is admin-only.

## Core endpoints

| Method | Path                    | Description                              |
|--------|--------------------------|-------------------------------------------|
| GET    | /api/users/me            | Current user profile                       |
| GET    | /api/users/{id}          | Get a user profile (self or admin)          |
| GET    | /api/users               | List users, paginated (admin only)           |
| POST   | /api/projects             | Create a project                               |
| GET    | /api/projects             | List your projects, paginated                   |
| GET    | /api/projects/{id}        | Get a project                                     |
| PUT    | /api/projects/{id}        | Update a project (owner or admin)                  |
| DELETE | /api/projects/{id}        | Delete a project (owner or admin)                   |
| POST   | /api/tasks                 | Create a task                                        |
| GET    | /api/tasks?projectId=      | List tasks for a project, paginated                   |
| PUT    | /api/tasks/{id}             | Update a task (project owner or admin)                  |
| DELETE | /api/tasks/{id}             | Delete a task (project owner or admin)                    |

List endpoints accept standard Spring pagination params: `?page=0&size=20&sort=name,asc`.

## Running tests

```bash
mvn test
```
