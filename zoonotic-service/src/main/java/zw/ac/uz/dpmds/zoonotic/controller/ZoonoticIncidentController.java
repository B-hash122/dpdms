package zw.ac.uz.dpmds.zoonotic.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentRequest;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentAuditResponse;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentResponse;
import zw.ac.uz.dpmds.zoonotic.service.ZoonoticIncidentAuditService;
import zw.ac.uz.dpmds.zoonotic.service.ZoonoticIncidentService;
import zw.ac.uz.dpmds.zoonotic.security.IncidentAuthorizationService;

import java.net.URI;
import java.util.List;

/**
 * Exposes REST operations for basic zoonotic incident management.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@RestController
@RequestMapping("/api/zoonotic-incidents")
@Tag(
        name = "Zoonotic incidents",
        description = "Backend-authorized zoonotic incident CRUD and workflow "
                + "operations. Spring Security remains authoritative for JWT "
                + "role, ward, and hazard scope.")
@SecurityRequirement(name = "bearerAuth")
public class ZoonoticIncidentController {

    private final ZoonoticIncidentService zoonoticIncidentService;
    private final ZoonoticIncidentAuditService auditService;
    private final IncidentAuthorizationService authorizationService;

    /**
     * Creates a controller backed by the incident service.
     *
     * @param zoonoticIncidentService the incident service
     */
    public ZoonoticIncidentController(
            ZoonoticIncidentService zoonoticIncidentService,
            ZoonoticIncidentAuditService auditService,
            IncidentAuthorizationService authorizationService) {
        this.zoonoticIncidentService = zoonoticIncidentService;
        this.auditService = auditService;
        this.authorizationService = authorizationService;
    }

    /**
     * Creates a new zoonotic incident.
     *
     * @param request the validated incident request
     * @return a created response with its resource location
     */
    @PostMapping
    @Operation(
            summary = "Create a pending incident",
            description = "WARD_RECORDER only. The request ward must match the "
                    + "recorder's authenticated ward and the ZOONOTIC hazard.")
    public ResponseEntity<ZoonoticIncidentResponse> createIncident(
            @Valid @RequestBody ZoonoticIncidentRequest request) {
        ZoonoticIncidentResponse response =
                zoonoticIncidentService.createIncident(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Retrieves one zoonotic incident.
     *
     * @param id the incident identifier
     * @return the requested incident
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get an incident",
            description = "WARD_RECORDER may access the assigned ward only. "
                    + "PROVINCIAL_SUPERVISOR may access all zoonotic wards.")
    public ResponseEntity<ZoonoticIncidentResponse> getIncidentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(zoonoticIncidentService.getIncidentById(id));
    }

    /**
     * Retrieves all zoonotic incidents.
     *
     * @return all incidents
     */
    @GetMapping
    @Operation(
            summary = "List incidents",
            description = "WARD_RECORDER receives only assigned-ward incidents. "
                    + "PROVINCIAL_SUPERVISOR may view incidents across wards.")
    public ResponseEntity<List<ZoonoticIncidentResponse>> getAllIncidents() {
        return ResponseEntity.ok(zoonoticIncidentService.getAllIncidents());
    }

    /**
     * Updates recorder-editable fields of an existing incident.
     *
     * @param id the incident identifier
     * @param request the validated incident request
     * @return the updated incident
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Update an incident",
            description = "WARD_RECORDER may update only an incident in the "
                    + "authenticated ward without changing its ward scope.")
    public ResponseEntity<ZoonoticIncidentResponse> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody ZoonoticIncidentRequest request) {
        return ResponseEntity.ok(
                zoonoticIncidentService.updateIncident(id, request));
    }

    /**
     * Deletes an existing zoonotic incident.
     *
     * @param id the incident identifier
     * @return an empty no-content response
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a pending incident",
            description = "WARD_RECORDER only. Deletes an owned PENDING incident; "
                    + "APPROVED, REJECTED, and CORRECTION_REQUESTED incidents "
                    + "return a workflow conflict.")
    public ResponseEntity<Void> deleteIncident(@PathVariable Long id) {
        zoonoticIncidentService.deleteIncident(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Approves a pending incident.
     *
     * @param id the incident identifier
     * @return the approved incident
     */
    @PatchMapping("/{id}/approve")
    @Operation(
            summary = "Approve an incident",
            description = "PROVINCIAL_SUPERVISOR only. Approval may initiate "
                    + "asynchronous alert integration when policy qualifies.")
    public ResponseEntity<ZoonoticIncidentResponse> approveIncident(
            @PathVariable Long id) {
        return ResponseEntity.ok(zoonoticIncidentService.approveIncident(id));
    }

    /**
     * Rejects a pending incident.
     *
     * @param id the incident identifier
     * @return the rejected incident
     */
    @PatchMapping("/{id}/reject")
    @Operation(
            summary = "Reject an incident",
            description = "PROVINCIAL_SUPERVISOR only.")
    public ResponseEntity<ZoonoticIncidentResponse> rejectIncident(
            @PathVariable Long id) {
        return ResponseEntity.ok(zoonoticIncidentService.rejectIncident(id));
    }

    /**
     * Requests corrections for a pending incident.
     *
     * @param id the incident identifier
     * @return the correction-requested incident
     */
    @PatchMapping("/{id}/request-correction")
    @Operation(
            summary = "Request incident correction",
            description = "PROVINCIAL_SUPERVISOR only. Returns the incident to "
                    + "the recorder's correction workflow.")
    public ResponseEntity<ZoonoticIncidentResponse> requestCorrection(
            @PathVariable Long id) {
        return ResponseEntity.ok(zoonoticIncidentService.requestCorrection(id));
    }

    /**
     * Resubmits an incident after corrections.
     *
     * @param id the incident identifier
     * @param request the validated corrected incident data
     * @return the pending resubmitted incident
     */
    @PutMapping("/{id}/resubmit")
    @Operation(
            summary = "Resubmit a corrected incident",
            description = "WARD_RECORDER only for an assigned-ward "
                    + "CORRECTION_REQUESTED incident.")
    public ResponseEntity<ZoonoticIncidentResponse> resubmitIncident(
            @PathVariable Long id,
            @Valid @RequestBody ZoonoticIncidentRequest request) {
        return ResponseEntity.ok(
                zoonoticIncidentService.resubmitIncident(id, request));
    }

    /**
     * Retrieves an incident's chronological audit history.
     *
     * @param id the incident identifier
     * @return the audit records
     */
    @GetMapping("/{id}/audit")
    @Operation(
            summary = "Get incident audit history",
            description = "PROVINCIAL_SUPERVISOR only.")
    public ResponseEntity<List<ZoonoticIncidentAuditResponse>> getAuditHistory(
            @PathVariable Long id) {
        authorizationService.requireSupervisor();
        return ResponseEntity.ok(auditService.getAuditHistory(id));
    }
}
