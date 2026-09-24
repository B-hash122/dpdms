package zw.ac.uz.dpmds.zoonotic.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentRequest;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentResponse;
import zw.ac.uz.dpmds.zoonotic.entity.AuditAction;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentScopeAccessDeniedException;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentDeletionNotAllowedException;
import zw.ac.uz.dpmds.zoonotic.exception.InvalidIncidentStatusTransitionException;
import zw.ac.uz.dpmds.zoonotic.exception.ZoonoticIncidentNotFoundException;
import zw.ac.uz.dpmds.zoonotic.repository.ZoonoticIncidentRepository;
import zw.ac.uz.dpmds.zoonotic.security.IncidentAuthorizationService;

import java.util.List;

/**
 * Provides basic business operations for zoonotic incidents.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@Service
public class ZoonoticIncidentService {

    private final ZoonoticIncidentRepository zoonoticIncidentRepository;
    private final ZoonoticIncidentAuditService auditService;
    private final ZoonoticAlertIntegrationService alertIntegrationService;
    private final IncidentAuthorizationService authorizationService;

    /**
     * Creates a service backed by the zoonotic incident repository.
     *
     * @param zoonoticIncidentRepository the incident repository
     */
    public ZoonoticIncidentService(
            ZoonoticIncidentRepository zoonoticIncidentRepository,
            ZoonoticIncidentAuditService auditService,
            ZoonoticAlertIntegrationService alertIntegrationService,
            IncidentAuthorizationService authorizationService) {
        this.zoonoticIncidentRepository = zoonoticIncidentRepository;
        this.auditService = auditService;
        this.alertIntegrationService = alertIntegrationService;
        this.authorizationService = authorizationService;
    }

    private void requireIncidentReadScope(ZoonoticIncident incident) {
        String recorderWard = authorizationService.getRecorderWardOrNull();
        if (recorderWard != null
                && !recorderWard.equals(incident.getWard())) {
            throw new IncidentScopeAccessDeniedException();
        }
    }

    /**
     * Creates a new pending zoonotic incident.
     *
     * @param request the recorder-submitted incident data
     * @return the saved incident response
     */
    public ZoonoticIncidentResponse createIncident(ZoonoticIncidentRequest request) {
        authorizationService.requireRecorderWard(request.getWard());
        ZoonoticIncident incident = mapRequestToIncident(request);
        incident.setStatus(IncidentStatus.PENDING);

        ZoonoticIncident savedIncident = zoonoticIncidentRepository.save(incident);
        auditService.record(
                savedIncident.getId(),
                AuditAction.INCIDENT_CREATED,
                null,
                IncidentStatus.PENDING);
        return mapIncidentToResponse(savedIncident);
    }

    /**
     * Retrieves a zoonotic incident by its identifier.
     *
     * @param id the incident identifier
     * @return the incident response
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     */
    public ZoonoticIncidentResponse getIncidentById(Long id) {
        ZoonoticIncident incident = findIncidentById(id);
        requireIncidentReadScope(incident);
        return mapIncidentToResponse(incident);
    }

    /**
     * Retrieves all zoonotic incidents.
     *
     * @return responses for all incidents
     */
    public List<ZoonoticIncidentResponse> getAllIncidents() {
        String recorderWard = authorizationService.getRecorderWardOrNull();
        List<ZoonoticIncident> incidents = recorderWard == null
                ? zoonoticIncidentRepository.findAll()
                : zoonoticIncidentRepository.findByWard(recorderWard);
        return incidents
                .stream()
                .map(this::mapIncidentToResponse)
                .toList();
    }

    /**
     * Updates recorder-editable data without changing workflow status or ID.
     *
     * @param id the incident identifier
     * @param request the updated recorder-submitted data
     * @return the updated incident response
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     */
    public ZoonoticIncidentResponse updateIncident(
            Long id,
            ZoonoticIncidentRequest request) {
        ZoonoticIncident incident = findIncidentById(id);
        authorizationService.requireRecorderIncidentScope(
                incident,
                request.getWard());
        IncidentStatus previousStatus = incident.getStatus();
        applyRequestToIncident(request, incident);

        ZoonoticIncident updatedIncident = zoonoticIncidentRepository.save(incident);
        auditService.record(
                updatedIncident.getId(),
                AuditAction.INCIDENT_UPDATED,
                previousStatus,
                updatedIncident.getStatus());
        return mapIncidentToResponse(updatedIncident);
    }

    /**
     * Deletes a zoonotic incident by its identifier.
     *
     * @param id the incident identifier
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     * @throws IncidentScopeAccessDeniedException when the recorder is outside
     * the incident ward
     * @throws IncidentDeletionNotAllowedException when the incident is not
     * pending
     */
    @Transactional
    public void deleteIncident(Long id) {
        ZoonoticIncident incident = findIncidentById(id);
        authorizationService.requireRecorderIncidentScope(
                incident,
                incident.getWard());
        if (incident.getStatus() != IncidentStatus.PENDING) {
            throw new IncidentDeletionNotAllowedException();
        }
        auditService.record(
                incident.getId(),
                AuditAction.INCIDENT_DELETED,
                incident.getStatus(),
                null);
        zoonoticIncidentRepository.delete(incident);
    }

    /**
     * Approves a pending incident.
     *
     * @param id the incident identifier
     * @return the approved incident response
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     * @throws InvalidIncidentStatusTransitionException for a non-pending incident
     */
    public ZoonoticIncidentResponse approveIncident(Long id) {
        return transitionIncident(
                id,
                IncidentStatus.APPROVED,
                AuditAction.INCIDENT_APPROVED);
    }

    /**
     * Rejects a pending incident.
     *
     * @param id the incident identifier
     * @return the rejected incident response
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     * @throws InvalidIncidentStatusTransitionException for a non-pending incident
     */
    public ZoonoticIncidentResponse rejectIncident(Long id) {
        return transitionIncident(
                id,
                IncidentStatus.REJECTED,
                AuditAction.INCIDENT_REJECTED);
    }

    /**
     * Requests corrections for a pending incident.
     *
     * @param id the incident identifier
     * @return the correction-requested incident response
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     * @throws InvalidIncidentStatusTransitionException for a non-pending incident
     */
    public ZoonoticIncidentResponse requestCorrection(Long id) {
        return transitionIncident(
                id,
                IncidentStatus.CORRECTION_REQUESTED,
                AuditAction.CORRECTION_REQUESTED);
    }

    /**
     * Resubmits an incident awaiting corrections.
     *
     * @param id the incident identifier
     * @param request the recorder-submitted corrected data
     * @return the resubmitted incident response
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     * @throws InvalidIncidentStatusTransitionException for a non-correction-requested incident
     */
    public ZoonoticIncidentResponse resubmitIncident(
            Long id,
            ZoonoticIncidentRequest request) {
        ZoonoticIncident incident = findIncidentById(id);
        authorizationService.requireRecorderIncidentScope(
                incident,
                request.getWard());
        ensureCurrentStatus(
                incident,
                IncidentStatus.CORRECTION_REQUESTED,
                IncidentStatus.PENDING);
        applyRequestToIncident(request, incident);
        incident.setStatus(IncidentStatus.PENDING);

        ZoonoticIncident resubmittedIncident = zoonoticIncidentRepository.save(incident);
        auditService.record(
                resubmittedIncident.getId(),
                AuditAction.INCIDENT_RESUBMITTED,
                IncidentStatus.CORRECTION_REQUESTED,
                IncidentStatus.PENDING);
        return mapIncidentToResponse(resubmittedIncident);
    }

    /**
     * Finds an incident or raises a domain-specific not-found exception.
     *
     * @param id the incident identifier
     * @return the persisted incident
     * @throws ZoonoticIncidentNotFoundException when no incident has the ID
     */
    private ZoonoticIncident findIncidentById(Long id) {
        return zoonoticIncidentRepository.findById(id)
                .orElseThrow(() -> new ZoonoticIncidentNotFoundException(id));
    }

    /**
     * Transitions a pending incident to an approval workflow status.
     *
     * @param id the incident identifier
     * @param requestedStatus the requested workflow status
     * @return the transitioned incident response
     */
    private ZoonoticIncidentResponse transitionIncident(
            Long id,
            IncidentStatus requestedStatus,
            AuditAction auditAction) {
        ZoonoticIncident incident = findIncidentById(id);
        authorizationService.requireSupervisor();
        ensureCurrentStatus(incident, IncidentStatus.PENDING, requestedStatus);
        IncidentStatus previousStatus = incident.getStatus();
        incident.setStatus(requestedStatus);

        ZoonoticIncident transitionedIncident = zoonoticIncidentRepository.save(incident);
        auditService.record(
                transitionedIncident.getId(),
                auditAction,
                previousStatus,
                requestedStatus);
        if (requestedStatus == IncidentStatus.APPROVED) {
            alertIntegrationService.publishApprovedIncident(
                    transitionedIncident,
                    alertIntegrationService.getCurrentBearerToken());
        }
        return mapIncidentToResponse(transitionedIncident);
    }

    /**
     * Ensures that an incident is in the required state before transitioning.
     *
     * @param incident the incident being transitioned
     * @param requiredStatus the required current status
     * @param requestedStatus the target status
     */
    private void ensureCurrentStatus(
            ZoonoticIncident incident,
            IncidentStatus requiredStatus,
            IncidentStatus requestedStatus) {
        if (incident.getStatus() != requiredStatus) {
            throw new InvalidIncidentStatusTransitionException(
                    incident.getId(),
                    incident.getStatus(),
                    requestedStatus);
        }
    }

    /**
     * Maps recorder-submitted data to a new entity.
     *
     * @param request the recorder-submitted incident data
     * @return a new incident entity
     */
    private ZoonoticIncident mapRequestToIncident(ZoonoticIncidentRequest request) {
        ZoonoticIncident incident = new ZoonoticIncident();
        applyRequestToIncident(request, incident);
        return incident;
    }

    /**
     * Applies recorder-editable data to an incident.
     *
     * @param request the recorder-submitted incident data
     * @param incident the incident to update
     */
    private void applyRequestToIncident(
            ZoonoticIncidentRequest request,
            ZoonoticIncident incident) {
        incident.setWard(request.getWard());
        incident.setDistrict(request.getDistrict());
        incident.setProvince(request.getProvince());
        incident.setOccurrenceDateTime(request.getOccurrenceDateTime());
        incident.setReporter(request.getReporter());
        incident.setSeverity(request.getSeverity());
        incident.setLatitude(request.getLatitude());
        incident.setLongitude(request.getLongitude());
        incident.setPathogenName(request.getPathogenName());
        incident.setAnimalSpeciesAffected(request.getAnimalSpeciesAffected());
        incident.setConfirmedHumanCases(request.getConfirmedHumanCases());
        incident.setConfirmedAnimalCases(request.getConfirmedAnimalCases());
        incident.setEventClassification(request.getEventClassification());
    }

    /**
     * Maps a persisted incident to its response representation.
     *
     * @param incident the persisted incident
     * @return the incident response
     */
    private ZoonoticIncidentResponse mapIncidentToResponse(ZoonoticIncident incident) {
        ZoonoticIncidentResponse response = new ZoonoticIncidentResponse();
        response.setId(incident.getId());
        response.setWard(incident.getWard());
        response.setDistrict(incident.getDistrict());
        response.setProvince(incident.getProvince());
        response.setOccurrenceDateTime(incident.getOccurrenceDateTime());
        response.setReporter(incident.getReporter());
        response.setSeverity(incident.getSeverity());
        response.setStatus(incident.getStatus());
        response.setLatitude(incident.getLatitude());
        response.setLongitude(incident.getLongitude());
        response.setPathogenName(incident.getPathogenName());
        response.setAnimalSpeciesAffected(incident.getAnimalSpeciesAffected());
        response.setConfirmedHumanCases(incident.getConfirmedHumanCases());
        response.setConfirmedAnimalCases(incident.getConfirmedAnimalCases());
        response.setEventClassification(incident.getEventClassification());
        return response;
    }
}
