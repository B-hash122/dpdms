# auth-service (port TBD)

**Owner:** Shebe (per Anesu's guide — pushes security config for the
gateway and each hazard API)

Responsibilities:
- User registration/login
- Issues signed JWTs carrying role (`FLOOD_RECORDER`, `FLOOD_SUPERVISOR`,
  `DROUGHT_RECORDER`, ..., `NATIONAL`, `ADMIN`) and ward, where applicable
- api-gateway validates the JWT and forwards `X-User-Role` / `X-User-Ward`
  headers downstream — every hazard service (see `flood-service`) already
  expects and enforces these headers, so once this lands the header-trust
  placeholder in each controller becomes real security.

Suggested port: **8081**.

## Checklist
- [ ] User entity + password hashing
- [ ] Login endpoint issuing JWT
- [ ] Gateway filter validating JWT and attaching X-User-Role / X-User-Ward
- [ ] Route registered in `api-gateway` (already stubbed as `/api/v1/auth/**`)
