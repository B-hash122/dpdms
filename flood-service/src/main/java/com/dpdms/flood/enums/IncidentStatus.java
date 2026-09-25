package com.dpdms.flood.enums;

/**
 * Approval workflow states (case study section "Access Control, Scoping,
 * and Approval Workflow"). Every new incident defaults to PENDING.
 *
 * PENDING              -> just captured by a ward recorder
 * APPROVED             -> approved by the flood provincial supervisor;
 *                         only APPROVED incidents may appear on the
 *                         dashboard, the map, or in generated reports
 * REJECTED             -> rejected by the supervisor, with a reason
 * CORRECTIONS_REQUESTED-> sent back to the recorder for editing and
 *                         resubmission
 */
public enum IncidentStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CORRECTIONS_REQUESTED
}
