package zw.ac.uz.dpmds.zoonotic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentScopeAccessDeniedException;
import zw.ac.uz.dpmds.zoonotic.security.HazardTypes;
import zw.ac.uz.dpmds.zoonotic.security.IncidentAuthorizationService;
import zw.ac.uz.dpmds.zoonotic.security.SecurityRoles;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies ward and hazard scope enforcement from authenticated JWT claims.
 *
 * @author DPDMS
 * @version 1.0
 */
class IncidentAuthorizationServiceTest {

    private final IncidentAuthorizationService service =
            new IncidentAuthorizationService();

    /**
     * Clears the security context between scope tests.
     */
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Ensures a recorder can use only the assigned ward.
     */
    @Test
    void recorderWardScopeIsEnforced() {
        authenticateRecorder("Ward A", HazardTypes.ZOONOTIC);
        ZoonoticIncident incident = incidentInWard("Ward A");

        assertDoesNotThrow(() -> service.requireRecorderIncidentScope(
                incident,
                "Ward A"));
        assertThrows(
                IncidentScopeAccessDeniedException.class,
                () -> service.requireRecorderIncidentScope(incident, "Ward B"));
    }

    /**
     * Ensures supervisors require the canonical zoonotic hazard.
     */
    @Test
    void supervisorHazardScopeIsEnforced() {
        authenticateSupervisor("OTHER_HAZARD");

        assertThrows(
                IncidentScopeAccessDeniedException.class,
                service::requireSupervisor);
    }

    private void authenticateRecorder(String ward, String hazard) {
        authenticate(
                SecurityRoles.WARD_RECORDER,
                Map.of("ward", ward, "hazard", hazard));
    }

    private void authenticateSupervisor(String hazard) {
        authenticate(
                SecurityRoles.PROVINCIAL_SUPERVISOR,
                Map.of("hazard", hazard));
    }

    private void authenticate(String role, Map<String, Object> claims) {
        Jwt jwt = new Jwt(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(900),
                Map.of("alg", "HS256"),
                claims);
        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(
                        jwt,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private ZoonoticIncident incidentInWard(String ward) {
        ZoonoticIncident incident = new ZoonoticIncident();
        incident.setWard(ward);
        return incident;
    }
}
