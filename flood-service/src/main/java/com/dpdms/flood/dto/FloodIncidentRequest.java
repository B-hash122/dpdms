package com.dpdms.flood.dto;

import com.dpdms.flood.enums.Severity;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * What a ward recorder submits when creating or updating a flood
 * incident. Deliberately excludes `status` and `id` — status is
 * controlled only through the approval endpoints, never set directly
 * by the recorder.
 */
@Getter
@Setter
public class FloodIncidentRequest {

    @NotBlank
    private String ward;

    @NotBlank
    private String district;

    @NotBlank
    private String province;

    @NotNull
    private LocalDateTime dateTime;

    @NotBlank
    private String reporter;

    @NotNull
    private Severity severity;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotNull
    @PositiveOrZero
    private Double peakWaterLevelMetres;

    @NotBlank
    private String riverBasin;

    @NotNull
    @PositiveOrZero
    private Integer householdsDisplaced;

    @NotNull
    @PositiveOrZero
    private Double areaFloodedHectares;

    @NotNull
    @PositiveOrZero
    private Integer durationDays;
}
