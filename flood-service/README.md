# flood-service

Flood incident microservice for the Rushinga Provincial Disaster Monitoring
and Management System (DPDMS). Owned by: **Anesu** (guide) / **Fidelis** (this build).

## What it does

- Full CRUD on flood incident records (`/api/v1/floods`)
- Shared incident metadata (ward, district, province, date/time, reporter,
  severity, status, GPS lat/lng) + the 5 flood-specific indicators:
  1. Peak water level (m)
  2. River basin / catchment name
  3. Households displaced
  4. Estimated area flooded (ha)
  5. Duration of inundation (days)
- Approval workflow: `PENDING -> APPROVED / REJECTED / CORRECTIONS_REQUESTED`
- Hazard/ward-scoped RBAC enforced in the controller (see below)
- Registers with Eureka (`discovery-service`) so the `api-gateway` can route
  `/api/v1/floods/**` to it
- Swagger/OpenAPI docs at `/swagger-ui.html`

## Running it

Requires JDK 17+ and Maven.

```bash
# 1. Start the discovery server first
cd discovery-service && mvn spring-boot:run

# 2. In another terminal, start the gateway
cd api-gateway && mvn spring-boot:run

# 3. In another terminal, start flood-service (uses in-memory H2 by default —
#    nothing else to configure for a quick run)
cd flood-service && mvn spring-boot:run
```

To point it at the shared MySQL instance instead of H2:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql \
  -DDB_HOST=localhost -DDB_NAME=flood_db -DDB_USER=root -DDB_PASSWORD=yourpassword
```

Then hit it either directly (`http://localhost:8082/api/v1/floods`) or through
the gateway (`http://localhost:8080/api/v1/floods`).

## API

| Method | Endpoint                                   | Role required                | Notes                                   |
|--------|---------------------------------------------|-------------------------------|------------------------------------------|
| GET    | `/api/v1/floods`                            | any                            | Recorders see own ward only; national/admin see APPROVED only unless `?status=` given |
| GET    | `/api/v1/floods/{id}`                       | any                            |                                          |
| POST   | `/api/v1/floods`                            | FLOOD_RECORDER, ADMIN          | Defaults to `PENDING`                   |
| PUT    | `/api/v1/floods/{id}`                       | FLOOD_RECORDER, ADMIN          | Only while `PENDING`/`CORRECTIONS_REQUESTED` |
| DELETE | `/api/v1/floods/{id}`                       | ADMIN                          |                                          |
| POST   | `/api/v1/floods/{id}/approve`               | FLOOD_SUPERVISOR, ADMIN        |                                          |
| POST   | `/api/v1/floods/{id}/reject`                | FLOOD_SUPERVISOR, ADMIN        | body: `{ "reason": "..." }` required    |
| POST   | `/api/v1/floods/{id}/request-corrections`   | FLOOD_SUPERVISOR, ADMIN        | body: `{ "reason": "..." }` required    |

## RBAC / hazard scoping

Every write is checked in `FloodController` itself — never only in the front
end — using two headers the gateway is expected to attach once auth-service
and Shebe's JWT filter are wired in:

- `X-User-Role`: `FLOOD_RECORDER`, `FLOOD_SUPERVISOR`, `NATIONAL`, or `ADMIN`
- `X-User-Ward`: required for recorders; a recorder for Ward 12 gets a
  `403 Forbidden` if they try to touch a Ward 9 record

A supervisor for a *different* hazard (e.g. `DROUGHT_SUPERVISOR`) calling
this service is also rejected with `403 Forbidden` — see
`FloodControllerTest.droughtSupervisor_cannotActOnFloodService`.

## Tests

`FloodControllerTest` covers the two rule families the brief flags as the
most bug-prone: ward/hazard scoping and the approval state machine.

```bash
mvn test
```

## Still to integrate with the rest of the group

- Real JWT validation (auth-service / Shebe) instead of trusted headers
- `alert-service` call from `approve()` for floods above the danger
  threshold (peak water level)
- `report-service` reading only `APPROVED` records for PDF/DOCX/XLSX/CSV export
- Audit trail table logging every state transition (who/when/what changed)
