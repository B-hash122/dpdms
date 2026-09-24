package zw.ac.uz.dpmds.zoonotic.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the workflow status of a zoonotic incident.
 *
 * @author DPDMS
 * @version 1.0
 */
@Schema(description = "Incident workflow status")
public enum IncidentStatus {
    PENDING,
    APPROVED,
    REJECTED,
    CORRECTION_REQUESTED
}
