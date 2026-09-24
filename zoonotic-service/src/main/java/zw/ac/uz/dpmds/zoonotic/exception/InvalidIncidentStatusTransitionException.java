package zw.ac.uz.dpmds.zoonotic.exception;

import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;

/**
 * Indicates that an incident cannot undergo the requested workflow transition.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class InvalidIncidentStatusTransitionException extends RuntimeException {

    /**
     * Creates an exception describing an invalid status transition.
     *
     * @param id the incident identifier
     * @param currentStatus the incident's current status
     * @param requestedStatus the requested status
     */
    public InvalidIncidentStatusTransitionException(
            Long id,
            IncidentStatus currentStatus,
            IncidentStatus requestedStatus) {
        super("Incident " + id + " cannot transition from "
                + currentStatus + " to " + requestedStatus);
    }
}
