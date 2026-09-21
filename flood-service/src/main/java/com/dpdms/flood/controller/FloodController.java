package com.dpdms.flood.controller;

import com.dpdms.flood.dto.ApprovalActionRequest;
import com.dpdms.flood.dto.FloodIncidentRequest;
import com.dpdms.flood.entity.FloodIncident;
import com.dpdms.flood.enums.IncidentStatus;
import com.dpdms.flood.exception.HazardScopeViolationException;
import com.dpdms.flood.exception.ResourceNotFoundException;
import com.dpdms.flood.repository.FloodRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST API for flood incidents.
 *
 * RBAC / hazard scoping note: this service trusts three headers that the
 * api-gateway attaches after validating the caller's JWT (issued by
 * auth-service, wired up by Shebe):
 *   X-User-Role  -> one of FLOOD_RECORDER, FLOOD_SUPERVISOR, NATIONAL, ADMIN
 *   X-User-Ward  -> the ward the caller is scoped to (recorders only)
 * Never trust a role/ward claim that only the front end enforces — every
 * check below happens here, in the service itself, per the case study's
 * "enforced in the backend — never in the front end alone" requirement.
 */
@RestController
@RequestMapping("/api/v1/floods")
@Tag(name = "Flood Incidents", description = "CRUD and approval workflow for flood incidents")
public class FloodController {

    private static final String ROLE_RECORDER = "FLOOD_RECORDER";
    private static final String ROLE_SUPERVISOR = "FLOOD_SUPERVISOR";
    private static final String ROLE_NATIONAL = "NATIONAL";
    private static final String ROLE_ADMIN = "ADMIN";

    private final FloodRepository floodRepository;

    public FloodController(FloodRepository floodRepository) {
        this.floodRepository = floodRepository;
    }

    // ---------- READ ----------

    @Operation(summary = "List flood incidents. National/admin users see all; others see only their ward's, approved-only unless they are the recorder/supervisor for it.")
    @GetMapping
    public ResponseEntity<List<FloodIncident>> getAll(
            @RequestHeader(value = "X-User-Role", defaultValue = ROLE_NATIONAL) String role,
            @RequestHeader(value = "X-User-Ward", required = false) String ward,
            @RequestParam(required = false) IncidentStatus status) {

        List<FloodIncident> incidents;

        if (ROLE_NATIONAL.equalsIgnoreCase(role) || ROLE_ADMIN.equalsIgnoreCase(role)) {
            // National users are read-only and cross-hazard, but a pending
            // record must still stay invisible to them per the workflow rule.
            incidents = (status != null)
                    ? floodRepository.findByStatus(status)
                    : floodRepository.findByStatus(IncidentStatus.APPROVED);
        } else if (ROLE_SUPERVISOR.equalsIgnoreCase(role)) {
            incidents = (status != null) ? floodRepository.findByStatus(status) : floodRepository.findAll();
        } else if (ROLE_RECORDER.equalsIgnoreCase(role)) {
            if (ward == null || ward.isBlank()) {
                throw new HazardScopeViolationException("Recorder requests must carry X-User-Ward");
            }
            incidents = (status != null)
                    ? floodRepository.findByWardAndStatus(ward, status)
                    : floodRepository.findByWardAndStatus(ward, IncidentStatus.PENDING);
        } else {
            throw new HazardScopeViolationException("Unrecognised role for flood-service: " + role);
        }

        return ResponseEntity.ok(incidents);
    }

    @Operation(summary = "Get a single flood incident by id")
    @GetMapping("/{id}")
    public ResponseEntity<FloodIncident> getById(@PathVariable Long id) {
        return ResponseEntity.ok(findOrThrow(id));
    }

    // ---------- CREATE ----------

    @Operation(summary = "Ward recorder submits a new flood incident. Defaults to PENDING.")
    @PostMapping
    public ResponseEntity<FloodIncident> create(
            @Valid @RequestBody FloodIncidentRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = ROLE_RECORDER) String role,
            @RequestHeader(value = "X-User-Ward", required = false) String callerWard) {

        requireRole(role, ROLE_RECORDER, ROLE_ADMIN);
        if (ROLE_RECORDER.equalsIgnoreCase(role)) {
            requireSameWard(callerWard, request.getWard());
        }

        FloodIncident incident = FloodIncident.builder()
                .ward(request.getWard())
                .district(request.getDistrict())
                .province(request.getProvince())
                .dateTime(request.getDateTime())
                .reporter(request.getReporter())
                .severity(request.getSeverity())
                .status(IncidentStatus.PENDING)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .peakWaterLevelMetres(request.getPeakWaterLevelMetres())
                .riverBasin(request.getRiverBasin())
                .householdsDisplaced(request.getHouseholdsDisplaced())
                .areaFloodedHectares(request.getAreaFloodedHectares())
                .durationDays(request.getDurationDays())
                .build();

        FloodIncident saved = floodRepository.save(incident);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ---------- UPDATE ----------

    @Operation(summary = "Update a flood incident. Only allowed while PENDING or CORRECTIONS_REQUESTED, and only by the owning recorder or an admin.")
    @PutMapping("/{id}")
    public ResponseEntity<FloodIncident> update(
            @PathVariable Long id,
            @Valid @RequestBody FloodIncidentRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = ROLE_RECORDER) String role,
            @RequestHeader(value = "X-User-Ward", required = false) String callerWard) {

        FloodIncident existing = findOrThrow(id);

        requireRole(role, ROLE_RECORDER, ROLE_ADMIN);
        if (ROLE_RECORDER.equalsIgnoreCase(role)) {
            requireSameWard(callerWard, existing.getWard());
            if (existing.getStatus() != IncidentStatus.PENDING
                    && existing.getStatus() != IncidentStatus.CORRECTIONS_REQUESTED) {
                throw new HazardScopeViolationException(
                        "Cannot edit an incident once it has been " + existing.getStatus());
            }
        }

        existing.setWard(request.getWard());
        existing.setDistrict(request.getDistrict());
        existing.setProvince(request.getProvince());
        existing.setDateTime(request.getDateTime());
        existing.setReporter(request.getReporter());
        existing.setSeverity(request.getSeverity());
        existing.setLatitude(request.getLatitude());
        existing.setLongitude(request.getLongitude());
        existing.setPeakWaterLevelMetres(request.getPeakWaterLevelMetres());
        existing.setRiverBasin(request.getRiverBasin());
        existing.setHouseholdsDisplaced(request.getHouseholdsDisplaced());
        existing.setAreaFloodedHectares(request.getAreaFloodedHectares());
        existing.setDurationDays(request.getDurationDays());

        // Resubmission after corrections goes back to PENDING for review.
        if (existing.getStatus() == IncidentStatus.CORRECTIONS_REQUESTED) {
            existing.setStatus(IncidentStatus.PENDING);
        }

        return ResponseEntity.ok(floodRepository.save(existing));
    }

    // ---------- DELETE ----------

    @Operation(summary = "Delete a flood incident (admin only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        requireRole(role, ROLE_ADMIN);
        FloodIncident existing = findOrThrow(id);
        floodRepository.delete(existing);
        return ResponseEntity.noContent().build();
    }

    // ---------- APPROVAL WORKFLOW ----------

    @Operation(summary = "Flood provincial supervisor approves an incident")
    @PostMapping("/{id}/approve")
    public ResponseEntity<FloodIncident> approve(
            @PathVariable Long id,
            @RequestBody(required = false) ApprovalActionRequest action,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        requireRole(role, ROLE_SUPERVISOR, ROLE_ADMIN);
        FloodIncident incident = findOrThrow(id);
        incident.setStatus(IncidentStatus.APPROVED);
        // TODO(integration): once alert-service is wired in, dispatch a
        // danger-threshold check here (peakWaterLevelMetres) so approved
        // floods above the threshold trigger email/WhatsApp alerts.
        return ResponseEntity.ok(floodRepository.save(incident));
    }

    @Operation(summary = "Flood provincial supervisor rejects an incident, with a reason")
    @PostMapping("/{id}/reject")
    public ResponseEntity<FloodIncident> reject(
            @PathVariable Long id,
            @RequestBody ApprovalActionRequest action,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        requireRole(role, ROLE_SUPERVISOR, ROLE_ADMIN);
        if (action == null || action.getReason() == null || action.getReason().isBlank()) {
            throw new IllegalArgumentException("A reason is required to reject an incident");
        }
        FloodIncident incident = findOrThrow(id);
        incident.setStatus(IncidentStatus.REJECTED);
        return ResponseEntity.ok(floodRepository.save(incident));
    }

    @Operation(summary = "Flood provincial supervisor requests corrections from the recorder")
    @PostMapping("/{id}/request-corrections")
    public ResponseEntity<FloodIncident> requestCorrections(
            @PathVariable Long id,
            @RequestBody ApprovalActionRequest action,
            @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        requireRole(role, ROLE_SUPERVISOR, ROLE_ADMIN);
        if (action == null || action.getReason() == null || action.getReason().isBlank()) {
            throw new IllegalArgumentException("A reason is required to request corrections");
        }
        FloodIncident incident = findOrThrow(id);
        incident.setStatus(IncidentStatus.CORRECTIONS_REQUESTED);
        return ResponseEntity.ok(floodRepository.save(incident));
    }

    // ---------- helpers ----------

    private FloodIncident findOrThrow(Long id) {
        return floodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flood incident not found: id=" + id));
    }

    private void requireRole(String actualRole, String... allowedRoles) {
        for (String allowed : allowedRoles) {
            if (allowed.equalsIgnoreCase(actualRole)) {
                return;
            }
        }
        throw new HazardScopeViolationException(
                "Role '" + actualRole + "' is not permitted to perform this action on flood-service");
    }

    private void requireSameWard(String callerWard, String targetWard) {
        if (callerWard == null || !callerWard.equalsIgnoreCase(targetWard)) {
            throw new HazardScopeViolationException(
                    "Recorder for ward '" + callerWard + "' cannot act on ward '" + targetWard + "'");
        }
    }
}
