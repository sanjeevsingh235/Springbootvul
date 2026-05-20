# Vulnerable Spring Boot Login Demo

This is an intentionally vulnerable local lab app. Do not deploy it on the internet or reuse these patterns in real software.

## Run

```bash
mvn spring-boot:run
```

Open <http://localhost:8081>.

Seeded users:

- `admin` / `admin123`
- `alice` / `password`

## Pages

- `GET /` home
- `GET /login` login page
- `POST /login` login action
- `GET /register` registration page
- `POST /register` create account
- `GET /accounts` account list and delete form

## API

```bash
curl http://localhost:8081/api/accounts
curl http://localhost:8081/api/accounts/1
curl http://localhost:8081/api/accounts/count
curl -X POST http://localhost:8081/api/accounts \
  -H 'Content-Type: application/json' \
  -d '{"username":"bob","password":"secret","email":"bob@example.local","role":"USER"}'
curl -X DELETE http://localhost:8081/api/accounts/1
curl -X DELETE 'http://localhost:8081/api/accounts?username=bob'
```

## Intentional Vulnerabilities

- Passwords are stored and returned in plaintext.
- SQL statements concatenate user input directly.
- Login has no rate limiting, session management, or lockout.
- APIs have no authentication or authorization checks.
- Registration allows role assignment by the client.
- HTML output renders user-controlled values without escaping.
- Delete-account actions do not require identity verification or CSRF protection.
- H2 console is enabled at `/h2-console`.
# Springbootvul
