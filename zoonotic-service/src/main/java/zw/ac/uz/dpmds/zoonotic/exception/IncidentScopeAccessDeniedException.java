package zw.ac.uz.dpmds.zoonotic.exception;

/**
 * Indicates that an authenticated user is outside the requested incident scope.
 *
 * @author DPDMS
 * @version 1.0
 */
public class IncidentScopeAccessDeniedException extends RuntimeException {

    /**
     * Creates a safe scope-denied error.
     */
    public IncidentScopeAccessDeniedException() {
        super("Access denied for the requested incident scope.");
    }
}
