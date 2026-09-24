package zw.ac.uz.dpmds.zoonotic.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents the severity level assigned to a zoonotic incident.
 *
 * @author DPDMS
 * @version 1.0
 */
@Schema(description = "Incident severity")
public enum Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
