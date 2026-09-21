# mining-service (port TBD)

**Owner:** _assign a teammate_

Captures mining accident incidents. Required indicators per the case
study brief:

1. Mine name and type (formal or artisanal)
2. Accident type (collapse, gas explosion, flooding, fall of ground)
3. Number of trapped or injured miners
4. Number of fatalities
5. Whether rescue operations are ongoing

Plus the shared incident metadata — see `flood-service` for the pattern
to copy (entity, controller, RBAC, tests).

Suggested port: **8086**.

## Checklist
- [ ] Entity + enums
- [ ] Repository
- [ ] Controller: full CRUD + approve/reject/request-corrections
- [ ] Ward/hazard RBAC enforced server-side
- [ ] Register route in `api-gateway` (already stubbed as `/api/v1/mining/**`)
- [ ] Unit tests for scoping + approval workflow
- [ ] README
