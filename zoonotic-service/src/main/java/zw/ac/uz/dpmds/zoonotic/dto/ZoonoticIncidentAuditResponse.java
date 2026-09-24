package zw.ac.uz.dpmds.zoonotic.dto;

import zw.ac.uz.dpmds.zoonotic.entity.AuditAction;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;

import java.time.LocalDateTime;

/**
 * Exposes a read-only representation of an incident audit event.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class ZoonoticIncidentAuditResponse {

    private Long id;
    private Long incidentId;
    private AuditAction action;
    private IncidentStatus previousStatus;
    private IncidentStatus newStatus;
    private String performedBy;
    private LocalDateTime performedAt;

    /**
     * Returns the audit identifier.
     *
     * @return the audit identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the audit identifier.
     *
     * @param id the audit identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the incident identifier.
     *
     * @return the incident identifier
     */
    public Long getIncidentId() {
        return incidentId;
    }

    /**
     * Sets the incident identifier.
     *
     * @param incidentId the incident identifier
     */
    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    /**
     * Returns the audit action.
     *
     * @return the audit action
     */
    public AuditAction getAction() {
        return action;
    }

    /**
     * Sets the audit action.
     *
     * @param action the audit action
     */
    public void setAction(AuditAction action) {
        this.action = action;
    }

    /**
     * Returns the previous status.
     *
     * @return the previous status
     */
    public IncidentStatus getPreviousStatus() {
        return previousStatus;
    }

    /**
     * Sets the previous status.
     *
     * @param previousStatus the previous status
     */
    public void setPreviousStatus(IncidentStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    /**
     * Returns the new status.
     *
     * @return the new status
     */
    public IncidentStatus getNewStatus() {
        return newStatus;
    }

    /**
     * Sets the new status.
     *
     * @param newStatus the new status
     */
    public void setNewStatus(IncidentStatus newStatus) {
        this.newStatus = newStatus;
    }

    /**
     * Returns who performed the action.
     *
     * @return the performer
     */
    public String getPerformedBy() {
        return performedBy;
    }

    /**
     * Sets who performed the action.
     *
     * @param performedBy the performer
     */
    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    /**
     * Returns when the action was performed.
     *
     * @return the performance timestamp
     */
    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    /**
     * Sets when the action was performed.
     *
     * @param performedAt the performance timestamp
     */
    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}
