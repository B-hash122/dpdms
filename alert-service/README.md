# alert-service (port TBD)

**Owner:** _assign a teammate_

Sends email + WhatsApp alerts asynchronously when an approved incident
meets a hazard-specific threshold (e.g. flood above danger water level,
an active fire, a disease cluster, a mining accident with fatalities).
Logs every alert: channel, recipient, timestamp, delivery status.

Credentials (email/WhatsApp API keys) must come from environment
variables, never hard-coded.

Suggested port: **8088**.

## Checklist
- [ ] Alert log entity (channel, recipient, timestamp, status)
- [ ] Async dispatch (e.g. `@Async` or a message queue) so it never blocks incident capture
- [ ] Email integration (JavaMail or transactional provider)
- [ ] WhatsApp Business Cloud API integration
- [ ] Route registered in `api-gateway` (already stubbed as `/api/v1/alerts/**`)
- [ ] Each hazard service's `approve()` endpoint calls this when thresholds are met
      (see the TODO in `flood-service/.../FloodController.java`)
