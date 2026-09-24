package zw.ac.uz.dpmds.zoonotic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import zw.ac.uz.dpmds.zoonotic.entity.AuditAction;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentDeletionNotAllowedException;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentScopeAccessDeniedException;
import zw.ac.uz.dpmds.zoonotic.exception.ZoonoticIncidentNotFoundException;
import zw.ac.uz.dpmds.zoonotic.repository.ZoonoticIncidentRepository;
import zw.ac.uz.dpmds.zoonotic.security.HazardTypes;
import zw.ac.uz.dpmds.zoonotic.security.SecurityRoles;
import zw.ac.uz.dpmds.zoonotic.service.ZoonoticAlertIntegrationService;
import zw.ac.uz.dpmds.zoonotic.service.ZoonoticIncidentAuditService;
import zw.ac.uz.dpmds.zoonotic.service.ZoonoticIncidentService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests secure, pending-only incident deletion and audit retention behavior.
 *
 * @author DPDMS
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ZoonoticIncidentDeletionTest {

    @Mock
    private ZoonoticIncidentRepository repository;

    @Mock
    private ZoonoticIncidentAuditService auditService;

    @Mock
    private ZoonoticAlertIntegrationService alertIntegrationService;

    /**
     * Clears the authenticated scope after each deletion test.
     */
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Ensures a recorder can delete an owned pending incident and audit first.
     */
    @Test
    void recorderCanDeleteOwnedPendingIncident() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident = incident("Ward A", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        service().deleteIncident(17L);

        InOrder order = inOrder(auditService, repository);
        order.verify(auditService).record(
                17L,
                AuditAction.INCIDENT_DELETED,
                IncidentStatus.PENDING,
                null);
        order.verify(repository).delete(incident);
    }

    /**
     * Ensures a recorder cannot delete another ward's incident.
     */
    @Test
    void recorderCannotDeleteAnotherWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().deleteIncident(17L));
        verify(repository, never()).delete(incident);
        verify(auditService, never()).record(
                17L,
                AuditAction.INCIDENT_DELETED,
                IncidentStatus.PENDING,
                null);
    }

    /**
     * Ensures a supervisor cannot use the recorder delete operation.
     */
    @Test
    void supervisorCannotDeleteIncident() {
        authenticateSupervisor();
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().deleteIncident(17L));
        verify(repository, never()).delete(incident);
    }

    /**
     * Ensures an unauthenticated service call is denied.
     */
    @Test
    void unauthenticatedDeleteIsDenied() {
        ZoonoticIncident incident = incident("Ward A", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().deleteIncident(17L));
        verify(repository, never()).delete(incident);
    }

    /**
     * Ensures a missing incident keeps the existing not-found behavior.
     */
    @Test
    void missingIncidentReturnsNotFoundFailure() {
        authenticateRecorder("Ward A");
        when(repository.findById(17L)).thenReturn(Optional.empty());

        assertThrows(
                ZoonoticIncidentNotFoundException.class,
                () -> service().deleteIncident(17L));
    }

    /**
     * Ensures approved incidents return a workflow conflict.
     */
    @Test
    void approvedIncidentCannotBeDeleted() {
        assertProtectedStatus(IncidentStatus.APPROVED);
    }

    /**
     * Ensures rejected incidents return a workflow conflict.
     */
    @Test
    void rejectedIncidentCannotBeDeleted() {
        assertProtectedStatus(IncidentStatus.REJECTED);
    }

    /**
     * Ensures correction-requested incidents return a workflow conflict.
     */
    @Test
    void correctionRequestedIncidentCannotBeDeleted() {
        assertProtectedStatus(IncidentStatus.CORRECTION_REQUESTED);
    }

    private void assertProtectedStatus(IncidentStatus status) {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident = incident("Ward A", status);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentDeletionNotAllowedException.class,
                () -> service().deleteIncident(17L));
        verify(repository, never()).delete(incident);
        verify(auditService, never()).record(
                17L,
                AuditAction.INCIDENT_DELETED,
                status,
                null);
    }

    private ZoonoticIncidentService service() {
        return new ZoonoticIncidentService(
                repository,
                auditService,
                alertIntegrationService,
                new zw.ac.uz.dpmds.zoonotic.security
                        .IncidentAuthorizationService());
    }

    private void authenticateRecorder(String ward) {
        authenticate(
                SecurityRoles.WARD_RECORDER,
                Map.of("ward", ward, "hazard", HazardTypes.ZOONOTIC));
    }

    private void authenticateSupervisor() {
        authenticate(
                SecurityRoles.PROVINCIAL_SUPERVISOR,
                Map.of("hazard", HazardTypes.ZOONOTIC));
    }

    private void authenticate(String role, Map<String, Object> claims) {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(900),
                Map.of("alg", "HS256"),
                claims);
        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(
                        jwt,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }

    private ZoonoticIncident incident(String ward, IncidentStatus status) {
        ZoonoticIncident incident = new ZoonoticIncident();
        incident.setId(17L);
        incident.setWard(ward);
        incident.setStatus(status);
        return incident;
    }
}
