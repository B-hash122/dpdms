package zw.ac.uz.dpmds.zoonotic.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.Objects;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.exception.IncidentScopeAccessDeniedException;

/**
 * Resolves authenticated incident scope and enforces zoonotic authorization.
 *
 * @author DPDMS
 * @version 1.0
 */
@Service
public class IncidentAuthorizationService {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String WARD_CLAIM = "ward";
    private static final String HAZARD_CLAIM = "hazard";

    /**
     * Requires a recorder assigned to this service's zoonotic hazard.
     *
     * @param requestedWard the ward submitted by the recorder
     */
    public void requireRecorderWard(String requestedWard) {
        Scope scope = currentScope();
        if (!scope.recorder() || !HazardTypes.ZOONOTIC.equals(scope.hazard())
                || !Objects.equals(scope.ward(), requestedWard)) {
            throw new IncidentScopeAccessDeniedException();
        }
    }

    /**
     * Requires a recorder to own the incident and keep it in the same ward.
     *
     * @param incident the existing incident
     * @param submittedWard the ward in the submitted request
     */
    public void requireRecorderIncidentScope(
            ZoonoticIncident incident,
            String submittedWard) {
        requireRecorderWard(submittedWard);
        if (!Objects.equals(scopeWard(), incident.getWard())) {
            throw new IncidentScopeAccessDeniedException();
        }
    }

    /**
     * Requires a supervisor assigned to this service's zoonotic hazard.
     */
    public void requireSupervisor() {
        Scope scope = currentScope();
        if (!scope.supervisor() || !HazardTypes.ZOONOTIC.equals(scope.hazard())) {
            throw new IncidentScopeAccessDeniedException();
        }
    }

    /**
     * Returns the recorder ward for repository filtering.
     *
     * @return the assigned ward, or null for a supervisor
     */
    public String getRecorderWardOrNull() {
        Scope scope = currentScope();
        if (scope.recorder() && HazardTypes.ZOONOTIC.equals(scope.hazard())) {
            return scope.ward();
        }
        if (scope.supervisor() && HazardTypes.ZOONOTIC.equals(scope.hazard())) {
            return null;
        }
        throw new IncidentScopeAccessDeniedException();
    }

    private String scopeWard() {
        return currentScope().ward();
    }

    private Scope currentScope() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new IncidentScopeAccessDeniedException();
        }
        boolean recorder = hasRole(authentication, SecurityRoles.WARD_RECORDER);
        boolean supervisor = hasRole(authentication, SecurityRoles.PROVINCIAL_SUPERVISOR);
        String ward = jwtAuthentication.getToken().getClaimAsString(WARD_CLAIM);
        String hazard = jwtAuthentication.getToken().getClaimAsString(HAZARD_CLAIM);
        return new Scope(recorder, supervisor, ward, hazard);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(ROLE_PREFIX + role));
    }

    private record Scope(
            boolean recorder,
            boolean supervisor,
            String ward,
            String hazard) {
    }
}
