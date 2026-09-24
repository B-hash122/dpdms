package zw.ac.uz.dpmds.zoonotic.service;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentAuditResponse;
import zw.ac.uz.dpmds.zoonotic.entity.AuditAction;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncidentAudit;
import zw.ac.uz.dpmds.zoonotic.repository.ZoonoticIncidentAuditRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Centralizes creation and retrieval of zoonotic incident audit records.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@Service
public class ZoonoticIncidentAuditService {

    /**
     * Temporary performer until Spring Security provides an authenticated principal.
     */
    public static final String SYSTEM_UNAUTHENTICATED = "SYSTEM_UNAUTHENTICATED";

    private final ZoonoticIncidentAuditRepository auditRepository;

    /**
     * Creates an audit service backed by the audit repository.
     *
     * @param auditRepository the audit repository
     */
    public ZoonoticIncidentAuditService(
            ZoonoticIncidentAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Records an incident action.
     *
     * @param incidentId the incident identifier
     * @param action the action performed
     * @param previousStatus the previous status
     * @param newStatus the resulting status
     */
    public void record(
            Long incidentId,
            AuditAction action,
            IncidentStatus previousStatus,
            IncidentStatus newStatus) {
        ZoonoticIncidentAudit audit = new ZoonoticIncidentAudit();
        audit.setIncidentId(incidentId);
        audit.setAction(action);
        audit.setPreviousStatus(previousStatus);
        audit.setNewStatus(newStatus);
        audit.setPerformedBy(getCurrentPerformer());
        audit.setPerformedAt(LocalDateTime.now());
        auditRepository.save(audit);
    }

    /**
     * Retrieves an incident's audit history in chronological order.
     *
     * @param incidentId the incident identifier
     * @return the audit history
     */
    public List<ZoonoticIncidentAuditResponse> getAuditHistory(Long incidentId) {
        return auditRepository.findByIncidentIdOrderByPerformedAtAsc(incidentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Maps a persistent audit record to its response DTO.
     *
     * @param audit the persistent audit record
     * @return the audit response
     */
    private ZoonoticIncidentAuditResponse mapToResponse(
            ZoonoticIncidentAudit audit) {
        ZoonoticIncidentAuditResponse response =
                new ZoonoticIncidentAuditResponse();
        response.setId(audit.getId());
        response.setIncidentId(audit.getIncidentId());
        response.setAction(audit.getAction());
        response.setPreviousStatus(audit.getPreviousStatus());
        response.setNewStatus(audit.getNewStatus());
        response.setPerformedBy(audit.getPerformedBy());
        response.setPerformedAt(audit.getPerformedAt());
        return response;
    }

    /**
     * Reads the authenticated username from the current security context.
     *
     * @return the authenticated username or the internal fallback value
     */
    private String getCurrentPerformer() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return SYSTEM_UNAUTHENTICATED;
    }
}
