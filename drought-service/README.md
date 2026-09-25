# drought-service (port TBD)

**Owner:** _assign a teammate_

Captures drought incidents. Required indicators per the case study brief:

1. Rainfall deficit (mm) against seasonal norm
2. Number of consecutive dry days
3. Crop failure percentage
4. Number of people facing water shortages
5. Livestock mortality count

Plus the shared incident metadata (ward, district, province, dateTime,
reporter, severity, status, latitude, longitude) — see `flood-service` for
a full worked example of the entity, controller, RBAC pattern, and tests
to copy the shape from.

Suggested port: **8083** (flood-service has 8082; keep hazard services
sequential so they're easy to remember).

## Checklist
- [ ] Entity + enums
- [ ] Repository
- [ ] Controller: full CRUD + approve/reject/request-corrections
- [ ] Ward/hazard RBAC enforced server-side
- [ ] Register route in `api-gateway/src/main/resources/application.yml`
      (already stubbed as `/api/v1/droughts/**`)
- [ ] Unit tests for scoping + approval workflow
- [ ] README
