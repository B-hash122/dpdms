package zw.ac.uz.dpmds.zoonotic.entity;

/**
 * Identifies an action recorded in the incident audit trail.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public enum AuditAction {
    INCIDENT_CREATED,
    INCIDENT_UPDATED,
    INCIDENT_DELETED,
    INCIDENT_APPROVED,
    INCIDENT_REJECTED,
    CORRECTION_REQUESTED,
    INCIDENT_RESUBMITTED
}
