# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
# Rushinga Provincial Disaster Monitoring and Management System (DPDMS)

HCS201/HCC201/HAI201 group assignment — University of Zimbabwe, due 29 September 2026.

## Team & ownership

| Service            | Port | Owner    | Status        |
|---------------------|------|----------|---------------|
| discovery-service   | 8761 | Fidelis  | ✅ Done        |
| api-gateway         | 8080 | Fidelis  | ✅ Routes stubbed |
| flood-service       | 8082 | Fidelis  | ✅ Done        |
| drought-service     | 8083 | _TBD_    | ⬜ Not started |
| fire-service        | 8084 | _TBD_    | ⬜ Not started |
| zoonotic-service    | 8085 | _TBD_    | ⬜ Not started |
| mining-service      | 8086 | _TBD_    | ⬜ Not started |
| auth-service        | 8081 | Shebe    | ⬜ Not started |
| report-service      | 8087 | _TBD_    | ⬜ Not started |
| alert-service       | 8088 | _TBD_    | ⬜ Not started |
| frontend            | —    | _TBD_    | ⬜ Not started |

Update this table as ownership is assigned and work progresses.

## Repo conventions

- **One folder per service** at the repo root, as listed above. Never nest
  a service inside another service's folder.
- **Branch per feature**, never commit straight to `main`:
  ```bash
  git checkout -b feature/<your-service>
  ```
- Open a **pull request** into `main` when your service is ready for
  review. Tag another group member as reviewer.
- **Only touch your own service folder**, plus (if needed) your own
  route block inside `api-gateway/src/main/resources/application.yml`.
  Routes for all 5 hazards are already stubbed there — just build against
  the one matching your hazard.
- Use `flood-service/` as the reference implementation: entity structure,
  DTOs, controller RBAC pattern, exception handling, and the test style
  are all in there to copy for drought/fire/zoonotic/mining.
- Pull `main` before starting new work each session, especially after
  Shebe pushes auth/security config:
  ```bash
  git checkout main && git pull origin main
  git checkout feature/<your-service> && git merge main
  ```

## Running the system locally

```bash
# 1. discovery-service first
cd discovery-service && mvn spring-boot:run

# 2. api-gateway
cd api-gateway && mvn spring-boot:run

# 3. any hazard service, e.g. flood-service (H2 by default, no DB setup needed)
cd flood-service && mvn spring-boot:run
```

Each service is independently runnable and registers itself with Eureka
(`http://localhost:8761`) so the gateway can route to it by name.

## Tech stack (mandatory, per the brief)

- Spring Boot for all backend services
- MySQL/PostgreSQL as the relational database (flood-service ships with
  an `mysql` profile — copy that pattern for your service)
- Spring Cloud Netflix Eureka (discovery-service) + Spring Cloud Gateway
  (api-gateway)
- Front-end: **TBD — decide as a group and document the justification in
  `frontend/README.md`**

## Submission checklist (from the brief)

- [ ] Source code for all 5 hazard services + auth/report/alert/dashboard/gateway/discovery
- [ ] Front-end application
- [ ] MySQL schema + seed data per service
- [ ] API documentation (Swagger/OpenAPI — flood-service already has this at `/swagger-ui.html`)
- [ ] Architecture diagram
- [ ] This README, kept up to date, explaining startup, approval workflow, RBAC, and alerting
- [ ] Peer evaluation form
