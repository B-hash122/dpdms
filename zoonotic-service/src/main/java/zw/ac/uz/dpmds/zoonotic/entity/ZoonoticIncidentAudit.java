package zw.ac.uz.dpmds.zoonotic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Stores an immutable audit event for a zoonotic incident operation.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@Entity
@Table(name = "zoonotic_incident_audits")
public class ZoonoticIncidentAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    private IncidentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private IncidentStatus newStatus;

    @Column(nullable = false)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    /**
     * Creates an empty audit record for JPA.
     */
    public ZoonoticIncidentAudit() {
    }

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
     * Returns the audited incident identifier.
     *
     * @return the incident identifier
     */
    public Long getIncidentId() {
        return incidentId;
    }

    /**
     * Sets the audited incident identifier.
     *
     * @param incidentId the incident identifier
     */
    public void setIncidentId(Long incidentId) {
        this.incidentId = incidentId;
    }

    /**
     * Returns the recorded action.
     *
     * @return the audit action
     */
    public AuditAction getAction() {
        return action;
    }

    /**
     * Sets the recorded action.
     *
     * @param action the audit action
     */
    public void setAction(AuditAction action) {
        this.action = action;
    }

    /**
     * Returns the previous workflow status.
     *
     * @return the previous status
     */
    public IncidentStatus getPreviousStatus() {
        return previousStatus;
    }

    /**
     * Sets the previous workflow status.
     *
     * @param previousStatus the previous status
     */
    public void setPreviousStatus(IncidentStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    /**
     * Returns the new workflow status.
     *
     * @return the new status
     */
    public IncidentStatus getNewStatus() {
        return newStatus;
    }

    /**
     * Sets the new workflow status.
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
