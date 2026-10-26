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

- `POST /api/auth/register` — create an account, returns a JWT.
- `POST /api/auth/login` — authenticate, returns a JWT.

Send the token on subsequent requests as `Authorization: Bearer <token>`.

## Core endpoints

| Method | Path                 | Description              |
|--------|-----------------------|---------------------------|
| GET    | /api/users/me          | Current user profile      |
| POST   | /api/projects          | Create a project          |
| GET    | /api/projects          | List your projects        |
| GET    | /api/projects/{id}     | Get a project              |
| PUT    | /api/projects/{id}     | Update a project            |
| DELETE | /api/projects/{id}     | Delete a project             |
| POST   | /api/tasks              | Create a task                |
| GET    | /api/tasks?projectId=  | List tasks for a project      |
| PUT    | /api/tasks/{id}         | Update a task                  |
| DELETE | /api/tasks/{id}         | Delete a task                    |

## Running tests

```bash
mvn test
```
