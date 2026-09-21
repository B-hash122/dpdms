# fire-service (port TBD)

**Owner:** _assign a teammate_

Captures fire incidents. Required indicators per the case study brief:

1. Area burned (hectares)
2. Suspected cause (natural / accidental / deliberate)
3. Number of injuries or fatalities
4. Structures destroyed
5. Whether the fire is still active or contained

Plus the shared incident metadata — see `flood-service` for the pattern
to copy (entity, controller, RBAC, tests).

Suggested port: **8084**.

## Checklist
- [ ] Entity + enums
- [ ] Repository
- [ ] Controller: full CRUD + approve/reject/request-corrections
- [ ] Ward/hazard RBAC enforced server-side
- [ ] Register route in `api-gateway` (already stubbed as `/api/v1/fires/**`)
- [ ] Unit tests for scoping + approval workflow
- [ ] README
