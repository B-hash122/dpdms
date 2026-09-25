# report-service (port TBD)

**Owner:** _assign a teammate_

Generates PDF, DOCX, XLSX, and CSV reports by calling each hazard
service's `GET /api/v1/{hazard}?status=APPROVED` endpoint (only approved
records may appear in reports, per the case study).

Filters required: hazard type, ward, district, date range, severity,
approval status.

Suggested port: **8087**.

## Checklist
- [ ] REST endpoint per format: `/api/v1/reports/pdf`, `/docx`, `/xlsx`, `/csv`
- [ ] Aggregates from all 5 hazard services via Eureka service discovery
- [ ] Filtering support
- [ ] Route registered in `api-gateway` (already stubbed as `/api/v1/reports/**`)
