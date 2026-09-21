package com.dpdms.flood.entity;

import com.dpdms.flood.enums.IncidentStatus;
import com.dpdms.flood.enums.Severity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A single flood incident record.
 *
 * Fields are split into two groups, matching the case study brief:
 *  1. Shared incident metadata (consistent across all 5 hazard services)
 *  2. The 5 flood-specific indicators Anesu's guide requires
 */
@Entity
@Table(name = "flood_incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloodIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- Shared incident metadata ----------

    @NotBlank(message = "Ward is required")
    @Column(nullable = false)
    private String ward;

    @NotBlank(message = "District is required")
    @Column(nullable = false)
    private String district;

    @NotBlank(message = "Province is required")
    @Column(nullable = false)
    private String province;

    @NotNull(message = "Date/time of occurrence is required")
    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @NotBlank(message = "Reporter is required")
    @Column(nullable = false)
    private String reporter;

    @NotNull(message = "Severity is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IncidentStatus status = IncidentStatus.PENDING;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    @Column(nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    @Column(nullable = false)
    private Double longitude;

    // ---------- Flood-specific indicators (Anesu's guide, indicators 1-5) ----------

    @NotNull(message = "Peak water level (m) is required")
    @PositiveOrZero
    @Column(name = "peak_water_level_m", nullable = false)
    private Double peakWaterLevelMetres;

    @NotBlank(message = "River basin / catchment name is required")
    @Column(name = "river_basin", nullable = false)
    private String riverBasin;

    @NotNull(message = "Number of households displaced is required")
    @PositiveOrZero
    @Column(name = "households_displaced", nullable = false)
    private Integer householdsDisplaced;

    @NotNull(message = "Estimated area flooded (ha) is required")
    @PositiveOrZero
    @Column(name = "area_flooded_ha", nullable = false)
    private Double areaFloodedHectares;

    @NotNull(message = "Duration of inundation (days) is required")
    @PositiveOrZero
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    // ---------- Audit ----------

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = IncidentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
