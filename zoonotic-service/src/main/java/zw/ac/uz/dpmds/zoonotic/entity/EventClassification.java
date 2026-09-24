package zw.ac.uz.dpmds.zoonotic.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Classifies the scale of a zoonotic disease event.
 *
 * @author DPDMS
 * @version 1.0
 */
@Schema(description = "Zoonotic event classification")
public enum EventClassification {
    CLUSTER,
    OUTBREAK
}
