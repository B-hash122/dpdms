# DPDMS Zoonotic Disease Service

The `zoonotic-service` is the Zoonotic Disease Incident Management component
of the DPDMS (Disaster Preparedness and Disaster Management System). It
records zoonotic disease incidents, applies server-side ward and hazard
authorization, manages review workflow, maintains an audit trail, and
publishes qualifying approved incidents to the alert service.

| Property | Value |
| --- | --- |
| Service name | `zoonotic-service` |
| Default port | `8085` |
| Direct incident API | `/api/zoonotic-incidents` |
| Gateway prefix | `/api/v1/zoonotic` |

## Technology stack

- Java 17 target
- Spring Boot 3.3.4
- Spring Cloud 2023.0.3
- Spring Web / REST
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server with JWT
- Spring Cloud Netflix Eureka Client
- MySQL for production persistence
- H2 for tests only
- Jakarta Bean Validation
- Spring Boot Actuator
- Springdoc OpenAPI / Swagger UI 2.6.0
- Maven

## Incident data

### Mandatory zoonotic indicators

Every incident request contains these five zoonotic indicators:

1. `pathogenName` - pathogen or disease name
2. `animalSpeciesAffected` - affected animal species
3. `confirmedHumanCases` - confirmed human case count
4. `confirmedAnimalCases` - confirmed animal case count
5. `eventClassification` - either `CLUSTER` or `OUTBREAK`

The two case counts must be zero or greater. Pathogen and species names are
required, and event classification is required.

### Shared incident metadata

The incident entity and API response also contain:

- `id`
- `ward`
- `district`
- `province`
- `occurrenceDateTime`
- `reporter`
- `severity`: `LOW`, `MEDIUM`, `HIGH`, or `CRITICAL`
- `latitude`: from `-90` to `90`
- `longitude`: from `-180` to `180`
- `status`: `PENDING`, `APPROVED`, `REJECTED`, or
  `CORRECTION_REQUESTED`

Required strings, dates, enums, coordinates, and case counts are validated
with Bean Validation before the request reaches the service layer.

## Roles and access control

The service recognizes two application roles:

- `WARD_RECORDER`
- `PROVINCIAL_SUPERVISOR`

Authorization is enforced in both the Spring Security filter chain and the
incident authorization service:

- A ward recorder must have the `WARD_RECORDER` role, a JWT `hazard` claim
  equal to `ZOONOTIC`, and a JWT `ward` claim matching the requested or
  existing incident ward.
- A provincial supervisor must have the `PROVINCIAL_SUPERVISOR` role and a
  JWT `hazard` claim equal to `ZOONOTIC`.
- Ward recorders can list only incidents in their assigned ward. Supervisors
  can list all zoonotic incidents.
- Ward recorders cannot move an existing incident to another ward.
- Supervisors do not require a ward claim for cross-ward review operations.
- Missing JWT authentication, an unsupported role, a missing/mismatched
  hazard claim, or an invalid ward scope results in access denial.

The JWT `roles` claim is converted to Spring authorities with the `ROLE_`
prefix. Business authorization is not delegated to the frontend or gateway.

## Incident workflow

New incidents are always created as `PENDING`.

The implemented supervisor transitions are:

```text
PENDING -> APPROVED
PENDING -> REJECTED
PENDING -> CORRECTION_REQUESTED
```

When a supervisor requests corrections, a ward recorder may submit corrected
data only while the incident is `CORRECTION_REQUESTED`. Resubmission changes
the status back to `PENDING`, after which the supervisor may review it again.
Updates preserve the incident identifier and current workflow status.

Deletion is restricted to a ward recorder operating within the incident's
authorized ward, and only `PENDING` incidents may be deleted. Approved,
rejected, and correction-requested incidents cannot be deleted. Workflow
operations are rejected when the incident is not in the required current
state.

Each create, update, delete, approval, rejection, correction request, and
resubmission records an audit event. Audit responses include the audit ID,
incident ID, action, previous status, new status, performer, and timestamp.

## REST API

The paths below are the direct service paths. The gateway provides a separate
external prefix described later.

### Authentication

| Method | Direct path | Purpose | Access |
| --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Authenticates a configured local development user and returns a signed JWT | Public |

The login response contains `accessToken`, `tokenType`, and `expiresIn`.
Credentials are submitted in the request body and are not documented here.

### Incident operations

| Method | Direct path | Purpose | Access |
| --- | --- | --- | --- |
| `POST` | `/api/zoonotic-incidents` | Creates a pending incident | `WARD_RECORDER`; JWT hazard must be `ZOONOTIC` and request ward must match JWT ward |
| `GET` | `/api/zoonotic-incidents` | Lists incidents | `WARD_RECORDER` or `PROVINCIAL_SUPERVISOR`; recorder results are ward-scoped |
| `GET` | `/api/zoonotic-incidents/{id}` | Retrieves one incident | `WARD_RECORDER` for its ward, or `PROVINCIAL_SUPERVISOR` |
| `PUT` | `/api/zoonotic-incidents/{id}` | Updates recorder-editable incident data | `WARD_RECORDER` for the same ward; submitted ward cannot change scope |
| `DELETE` | `/api/zoonotic-incidents/{id}` | Deletes an incident | `WARD_RECORDER` for the same ward, and only while `PENDING` |
| `PATCH` | `/api/zoonotic-incidents/{id}/approve` | Approves a pending incident | `PROVINCIAL_SUPERVISOR`; hazard must be `ZOONOTIC` |
| `PATCH` | `/api/zoonotic-incidents/{id}/reject` | Rejects a pending incident | `PROVINCIAL_SUPERVISOR`; hazard must be `ZOONOTIC` |
| `PATCH` | `/api/zoonotic-incidents/{id}/request-correction` | Requests corrections on a pending incident | `PROVINCIAL_SUPERVISOR`; hazard must be `ZOONOTIC` |
| `PUT` | `/api/zoonotic-incidents/{id}/resubmit` | Resubmits corrected data as pending | `WARD_RECORDER` for the same ward, and only from `CORRECTION_REQUESTED` |
| `GET` | `/api/zoonotic-incidents/{id}/audit` | Retrieves chronological audit history | `PROVINCIAL_SUPERVISOR`; hazard must be `ZOONOTIC` |

The controller also exposes the following public technical endpoints:

| Method | Direct path | Purpose |
| --- | --- | --- |
| `GET` | `/actuator/health` | Health status |
| `GET` | `/v3/api-docs` and `/v3/api-docs/**` | OpenAPI document |
| `GET` | `/swagger-ui.html` and `/swagger-ui/**` | Swagger UI |

All other requests require authentication. Validation failures are returned
as structured bad-request responses. Domain errors include not found,
forbidden scope, invalid workflow transition, and deletion-not-allowed
responses.

## API Gateway integration

The API Gateway route uses:

```text
External prefix: /api/v1/zoonotic/**
Service URI:     lb://zoonotic-service
```

The Gateway `RewritePath` filter maps the external route to the existing
controller structure without renaming the direct service endpoints:

```text
/api/v1/zoonotic
    -> /api/zoonotic-incidents

/api/v1/zoonotic/{suffix}
    -> /api/zoonotic-incidents/{suffix}
```

For example, `/api/v1/zoonotic/42/audit` is sent to
`/api/zoonotic-incidents/42/audit`.

## Eureka integration

The service registers with Eureka using the application name
`zoonotic-service`. The default Eureka server is:

```text
http://localhost:8761/eureka/
```

The service prefers its IP address when registering. The Eureka URL can be
overridden with `EUREKA_DEFAULT_ZONE`.

## Database configuration

Production configuration uses MySQL:

```text
jdbc:mysql://localhost:3306/zoonotic_disease
```

The schema is managed with JPA/Hibernate `update` mode and SQL logging is
enabled by the current production properties. The database username and
password are supplied externally; no credentials are included in this
README.

The test profile uses an in-memory H2 database in MySQL compatibility mode
with `create-drop` schema handling. H2 is test-only and does not replace the
production MySQL configuration.

## Environment variables

The following environment variables are referenced by the service. Set them
to deployment-specific values; examples below are placeholders, not
credentials.

| Variable | Purpose |
| --- | --- |
| `ZOONOTIC_SERVICE_PORT` | Overrides the HTTP port; defaults to `8085` |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | Secret used to sign and validate local JWTs |
| `JWT_EXPIRATION_SECONDS` | JWT lifetime in seconds; defaults to `900` |
| `WARD_RECORDER_WARD` | Ward assigned to the configured development recorder; defaults to a configured example ward |
| `RECORDER_PASSWORD` | Password for the configured local recorder user |
| `SUPERVISOR_PASSWORD` | Password for the configured local supervisor user |
| `ALERT_SERVICE_URL` | Alert-service base URL; defaults to the local alert-service address |
| `EUREKA_DEFAULT_ZONE` | Eureka server URL; defaults to `http://localhost:8761/eureka/` |

Do not commit real passwords, JWT secrets, or database credentials. The test
profile supplies isolated test settings and must not be treated as a
production configuration.

## Security and JWT claims

Spring Security configures the service as a stateless OAuth2 Resource Server.
Bearer JWTs are validated with the configured HMAC secret and converted into
authorities from the `roles` claim.

Tokens issued by the local login controller include:

- `roles`: one or more application roles
- `hazard`: `ZOONOTIC`
- `ward`: included for a ward recorder token
- standard subject, issued-at, and expiry claims

The token expiry is controlled by `JWT_EXPIRATION_SECONDS` and defaults to
900 seconds. The signing secret and development-user passwords are read from
environment-backed configuration. No credentials or secrets are documented
here.

## Alert integration

After a successful supervisor approval, the service evaluates the approved
incident for alerting. An incident qualifies when at least one of these
conditions is true:

- event classification is `OUTBREAK`
- severity is `CRITICAL`
- confirmed human cases are greater than zero

Qualifying incidents are sent asynchronously with `POST` to
`/api/alerts/zoonotic` at the configured `alert.service.url`. The payload
contains the incident ID, `ZOONOTIC` hazard type, zoonotic indicators,
location metadata, severity, event classification, case counts, and
approved status. When the current request has a JWT, its bearer token is
forwarded to the alert service.

This is an asynchronous HTTP integration, not a durable message queue.
Approval is not rolled back if the external call fails; the failure is
logged. The alert service and its reachable URL are external infrastructure
that must be running for delivery to occur.

## Reporting

The zoonotic backend contains no report-service integration or reporting
endpoint. Reporting, if provided by the wider DPDMS, is handled outside this
service.

## OpenAPI, Swagger, and Actuator

OpenAPI and Swagger UI are provided by Springdoc:

```text
OpenAPI JSON: http://localhost:8085/v3/api-docs
Swagger UI:   http://localhost:8085/swagger-ui.html
```

Actuator exposes health and info endpoints through the configured management
exposure:

```text
Health: http://localhost:8085/actuator/health
Info:   http://localhost:8085/actuator/info
```

Health is explicitly public. Business endpoints remain protected.

## Testing

The existing tests cover:

- `IncidentAuthorizationServiceTest` - direct ward and hazard authorization
  decisions
- `JwtServiceTest` - JWT roles, hazard, ward scope, and expiry-related token
  data
- `OpenApiAndHealthTest` - public health and OpenAPI access and protected
  business access
- `ZoonoticAlertIntegrationServiceTest` - alert qualification policy and
  bearer-token behavior
- `ZoonoticDiseaseServiceApplicationTests` - application context loading
- `ZoonoticIncidentAuthorizationTest` - recorder/supervisor CRUD and workflow
  authorization, ward scoping, and alert invocation
- `ZoonoticIncidentDeletionTest` - pending-only deletion and deletion access
  restrictions

The current verified result is:

```text
35 tests passed
0 failed
0 skipped
Maven package succeeded
```

Run the service test suite from this directory with:

```powershell
.\mvnw.cmd test
```

Build the packaged application with:

```powershell
.\mvnw.cmd package
```

## Running locally

### Prerequisites

- Java 17
- Maven, or use the included Maven Wrapper
- MySQL for non-test execution
- Environment variables for database, JWT, and development-user settings
- Eureka if service registration is required
- The alert service if approved-incident alert delivery is required

### Start the service

From `zoonotic-service`:

```powershell
.\mvnw.cmd spring-boot:run
```

The default service URL is `http://localhost:8085`.

For full group integration, the relevant platform components are:

```text
discovery-service   http://localhost:8761
api-gateway         http://localhost:8080
zoonotic-service    http://localhost:8085
```

### Gateway request path

An external request follows this route:

```text
/api/v1/zoonotic
    -> API Gateway
    -> Eureka lookup of zoonotic-service
    -> /api/zoonotic-incidents
```

The same suffix-preserving rewrite applies to incident IDs, workflow
operations, and audit requests.

## Project/package structure

```text
src/main/java/zw/ac/uz/dpmds/zoonotic/
├── config/       OpenAPI metadata and security scheme configuration
├── controller/   Authentication and incident REST controllers
├── dto/          Request, response, login, alert, and audit representations
├── entity/       JPA entities and incident enums
├── exception/    Domain exceptions and structured REST error handling
├── repository/   Spring Data JPA repositories
├── security/     JWT, role conversion, hazard/ward scope, and HTTP security
└── service/      Incident workflow, audit, and alert integration logic

src/test/
├── java/         Unit, context, security, workflow, and integration tests
└── resources/    Isolated H2 test-profile configuration
```

## Design decisions

- **Microservice separation:** zoonotic incidents have domain-specific
  indicators, workflow, authorization, and alert criteria, so they are
  isolated behind a dedicated deployable service.
- **DTOs:** request and response DTOs separate the HTTP contract from JPA
  entities and allow validation and alert payloads to evolve independently.
- **Server-side RBAC and scoping:** role, hazard, and ward checks are enforced
  in backend code so a client cannot bypass authorization by changing a
  frontend request.
- **JWT:** stateless bearer tokens carry authenticated roles and the
  zoonotic ward/hazard scope required by the service.
- **Audit records:** workflow and data changes are persisted as ordered audit
  events for traceability.
- **Environment variables:** deployment-specific database, secret, password,
  port, discovery, and integration settings remain outside source control.
- **Eureka/service discovery:** the service registers as `zoonotic-service`,
  allowing platform clients to resolve it without a fixed service host.
- **Gateway routing:** external API versioning is kept at the gateway while
  existing direct controller paths remain stable for service clients and
  tests.
- **Relational persistence:** JPA and MySQL provide durable storage for
  structured incidents and audit history.
- **Validation:** Bean Validation rejects missing, negative, or out-of-range
  incident data before business processing.
- **Automated tests:** focused tests protect authorization, workflow,
  deletion, JWT, alert, health, documentation, and application-context
  behavior.

## Known integration considerations

- Eureka must be available at the configured Eureka URL for registration and
  gateway load-balanced discovery.
- Production startup requires a reachable MySQL database and externally
  supplied database/JWT/development-user configuration.
- Alert delivery requires a reachable alert service at `ALERT_SERVICE_URL`;
  the integration is asynchronous and does not provide durable retries.
- The gateway route depends on the `zoonotic-service` Eureka service ID and
  its configured `RewritePath` filter.
- The service exposes local development login users configured through
  environment-backed properties; those credentials must be managed securely
  outside the repository.
- The backend does not provide report-service functionality.
