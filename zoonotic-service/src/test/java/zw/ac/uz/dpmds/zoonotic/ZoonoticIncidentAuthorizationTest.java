package zw.ac.uz.dpmds.zoonotic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticIncidentRequest;
import zw.ac.uz.dpmds.zoonotic.entity.EventClassification;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.Severity;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentScopeAccessDeniedException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests incident service enforcement for recorder and supervisor scopes.
 *
 * @author DPDMS
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class ZoonoticIncidentAuthorizationTest {

    @Mock
    private ZoonoticIncidentRepository repository;

    @Mock
    private ZoonoticIncidentAuditService auditService;

    @Mock
    private ZoonoticAlertIntegrationService alertIntegrationService;

    /**
     * Clears authentication after each service authorization test.
     */
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Ensures recorder creation is limited to the assigned ward.
     */
    @Test
    void recorderCannotCreateOutsideAssignedWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncidentService service = service();

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service.createIncident(request("Ward B")));
        verify(repository, never()).save(any(ZoonoticIncident.class));
    }

    /**
     * Ensures recorder creation succeeds inside the assigned ward.
     */
    @Test
    void recorderCanCreateInAssignedWard() {
        authenticateRecorder("Ward A");
        when(repository.save(any(ZoonoticIncident.class)))
                .thenAnswer(invocation -> {
                    ZoonoticIncident saved = invocation.getArgument(0);
                    saved.setId(17L);
                    return saved;
                });

        assertDoesNotThrow(() -> service().createIncident(request("Ward A")));
        verify(repository).save(any(ZoonoticIncident.class));
    }

    /**
     * Ensures recorder list queries are filtered in the repository layer.
     */
    @Test
    void recorderListUsesAssignedWardFilter() {
        authenticateRecorder("Ward A");
        when(repository.findByWard("Ward A")).thenReturn(List.of());

        service().getAllIncidents();

        verify(repository).findByWard("Ward A");
        verify(repository, never()).findAll();
    }

    /**
     * Ensures recorder access to another ward is forbidden.
     */
    @Test
    void recorderCannotReadAnotherWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().getIncidentById(17L));
    }

    /**
     * Ensures recorder can retrieve an incident in the assigned ward.
     */
    @Test
    void recorderCanReadAssignedWard() {
        authenticateRecorder("Ward A");
        when(repository.findById(17L)).thenReturn(
                Optional.of(incident("Ward A", IncidentStatus.PENDING)));

        assertDoesNotThrow(() -> service().getIncidentById(17L));
    }

    /**
     * Ensures recorder updates cannot move an incident across wards.
     */
    @Test
    void recorderCannotUpdateAnotherWardOrMoveWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident = incident("Ward A", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().updateIncident(17L, request("Ward B")));
        assertEquals("Ward A", incident.getWard());
    }

    /**
     * Ensures recorder can update an incident in the assigned ward.
     */
    @Test
    void recorderCanUpdateAssignedWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident = incident("Ward A", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));
        when(repository.save(any(ZoonoticIncident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(
                () -> service().updateIncident(17L, request("Ward A")));
        verify(repository).save(incident);
    }

    /**
     * Ensures recorder resubmission remains ward-scoped.
     */
    @Test
    void recorderCannotResubmitAnotherWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident =
                incident("Ward B", IncidentStatus.CORRECTION_REQUESTED);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().resubmitIncident(17L, request("Ward B")));
    }

    /**
     * Ensures recorder can resubmit a correction in the assigned ward.
     */
    @Test
    void recorderCanResubmitAssignedWard() {
        authenticateRecorder("Ward A");
        ZoonoticIncident incident =
                incident("Ward A", IncidentStatus.CORRECTION_REQUESTED);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));
        when(repository.save(any(ZoonoticIncident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service().resubmitIncident(17L, request("Ward A"));

        assertEquals(IncidentStatus.PENDING, incident.getStatus());
    }

    /**
     * Ensures a supervisor can list and read incidents across wards.
     */
    @Test
    void supervisorCanReadAcrossWards() {
        authenticateSupervisor();
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        when(repository.findAll()).thenReturn(List.of(incident));
        when(repository.findById(17L)).thenReturn(Optional.of(incident));

        assertEquals(1, service().getAllIncidents().size());
        service().getIncidentById(17L);

        verify(repository).findAll();
        verify(repository).findById(17L);
    }

    /**
     * Ensures a supervisor cannot use recorder creation privileges.
     */
    @Test
    void supervisorCannotCreateIncident() {
        authenticateSupervisor();

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service().createIncident(request("Ward A")));
    }

    /**
     * Ensures supervisor approval remains available and triggers alert integration.
     */
    @Test
    void supervisorCanApproveAndPreserveAlertIntegration() {
        authenticateSupervisor();
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        incident.setEventClassification(EventClassification.OUTBREAK);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));
        when(repository.save(any(ZoonoticIncident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(alertIntegrationService.getCurrentBearerToken()).thenReturn("token");

        service().approveIncident(17L);

        assertEquals(IncidentStatus.APPROVED, incident.getStatus());
        verify(auditService).record(
                17L,
                zw.ac.uz.dpmds.zoonotic.entity.AuditAction.INCIDENT_APPROVED,
                IncidentStatus.PENDING,
                IncidentStatus.APPROVED);
        verify(alertIntegrationService).publishApprovedIncident(incident, "token");
    }

    /**
     * Ensures supervisor rejection remains available.
     */
    @Test
    void supervisorCanReject() {
        authenticateSupervisor();
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));
        when(repository.save(any(ZoonoticIncident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service().rejectIncident(17L);

        assertEquals(IncidentStatus.REJECTED, incident.getStatus());
    }

    /**
     * Ensures supervisor correction requests remain available.
     */
    @Test
    void supervisorCanRequestCorrection() {
        authenticateSupervisor();
        ZoonoticIncident incident = incident("Ward B", IncidentStatus.PENDING);
        when(repository.findById(17L)).thenReturn(Optional.of(incident));
        when(repository.save(any(ZoonoticIncident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service().requestCorrection(17L);

        assertEquals(
                IncidentStatus.CORRECTION_REQUESTED,
                incident.getStatus());
    }

    private ZoonoticIncidentService service() {
        return new ZoonoticIncidentService(
                repository,
                auditService,
                alertIntegrationService,
                new zw.ac.uz.dpmds.zoonotic.security.IncidentAuthorizationService());
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

    private ZoonoticIncidentRequest request(String ward) {
        ZoonoticIncidentRequest request = new ZoonoticIncidentRequest();
        request.setWard(ward);
        request.setDistrict("District");
        request.setProvince("Province");
        request.setOccurrenceDateTime(java.time.LocalDateTime.now());
        request.setReporter("Recorder");
        request.setSeverity(Severity.HIGH);
        request.setLatitude(0.0);
        request.setLongitude(0.0);
        request.setPathogenName("Rabies");
        request.setAnimalSpeciesAffected("Dogs");
        request.setConfirmedHumanCases(0L);
        request.setConfirmedAnimalCases(1L);
        request.setEventClassification(EventClassification.CLUSTER);
        return request;
    }

    private ZoonoticIncident incident(String ward, IncidentStatus status) {
        ZoonoticIncident incident = new ZoonoticIncident();
        incident.setId(17L);
        incident.setWard(ward);
        incident.setStatus(status);
        incident.setSeverity(Severity.HIGH);
        incident.setEventClassification(EventClassification.CLUSTER);
        incident.setConfirmedHumanCases(0);
        incident.setConfirmedAnimalCases(1);
        return incident;
    }
}
