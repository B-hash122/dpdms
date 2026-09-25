# zoonotic-service (port TBD)

**Owner:** _assign a teammate_

Captures zoonotic disease incidents. Required indicators per the case
study brief:

1. Pathogen or disease name (e.g. anthrax, rabies, brucellosis)
2. Animal species affected
3. Number of confirmed human cases
4. Number of confirmed animal cases
5. Whether classified as a cluster or an outbreak

Plus the shared incident metadata — see `flood-service` for the pattern
to copy (entity, controller, RBAC, tests).

Suggested port: **8085**.

## Checklist
- [ ] Entity + enums
- [ ] Repository
- [ ] Controller: full CRUD + approve/reject/request-corrections
- [ ] Ward/hazard RBAC enforced server-side
- [ ] Register route in `api-gateway` (already stubbed as `/api/v1/zoonotic/**`)
- [ ] Unit tests for scoping + approval workflow
- [ ] README
