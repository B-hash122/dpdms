package zw.ac.uz.dpmds.zoonotic.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import zw.ac.uz.dpmds.zoonotic.entity.EventClassification;
import zw.ac.uz.dpmds.zoonotic.entity.Severity;

import java.time.LocalDateTime;

/**
 * Carries recorder-submitted data for a new zoonotic incident.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class ZoonoticIncidentRequest {

    private static final long MIN_CASE_COUNT = 0L;

    @Schema(description = "Authorized ward for the recorder submission", example = "Ward 5")
    @NotBlank
    private String ward;

    @Schema(example = "Rushinga")
    @NotBlank
    private String district;

    @Schema(example = "Mashonaland Central")
    @NotBlank
    private String province;

    @Schema(description = "Date and time of occurrence", example = "2026-09-20T14:30:00")
    @NotNull
    private LocalDateTime occurrenceDateTime;

    @Schema(example = "Ward Recorder")
    @NotBlank
    private String reporter;

    @Schema(description = "Incident severity")
    @NotNull
    private Severity severity;

    @Schema(description = "Latitude from -90 to 90", example = "-16.6167")
    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    @Schema(description = "Longitude from -180 to 180", example = "32.0167")
    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @Schema(description = "Pathogen or disease name", example = "Rabies")
    @NotBlank
    private String pathogenName;

    @Schema(description = "Animal species affected", example = "Dogs")
    @NotBlank
    private String animalSpeciesAffected;

    @Schema(description = "Confirmed human case count", example = "2", minimum = "0")
    @NotNull
    @Min(MIN_CASE_COUNT)
    private Long confirmedHumanCases;

    @Schema(description = "Confirmed animal case count", example = "5", minimum = "0")
    @NotNull
    @Min(MIN_CASE_COUNT)
    private Long confirmedAnimalCases;

    @Schema(description = "Event classification")
    @NotNull
    private EventClassification eventClassification;

    /**
     * Returns the ward where the incident occurred.
     *
     * @return the ward
     */
    public String getWard() {
        return ward;
    }

    /**
     * Sets the ward where the incident occurred.
     *
     * @param ward the ward
     */
    public void setWard(String ward) {
        this.ward = ward;
    }

    /**
     * Returns the district where the incident occurred.
     *
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the district where the incident occurred.
     *
     * @param district the district
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * Returns the province where the incident occurred.
     *
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the province where the incident occurred.
     *
     * @param province the province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Returns when the incident occurred.
     *
     * @return the occurrence date and time
     */
    public LocalDateTime getOccurrenceDateTime() {
        return occurrenceDateTime;
    }

    /**
     * Sets when the incident occurred.
     *
     * @param occurrenceDateTime the occurrence date and time
     */
    public void setOccurrenceDateTime(LocalDateTime occurrenceDateTime) {
        this.occurrenceDateTime = occurrenceDateTime;
    }

    /**
     * Returns the incident reporter.
     *
     * @return the reporter
     */
    public String getReporter() {
        return reporter;
    }

    /**
     * Sets the incident reporter.
     *
     * @param reporter the reporter
     */
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    /**
     * Returns the incident severity.
     *
     * @return the severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Sets the incident severity.
     *
     * @param severity the severity
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    /**
     * Returns the incident latitude.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the incident latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the incident longitude.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the incident longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the pathogen or disease name.
     *
     * @return the pathogen name
     */
    public String getPathogenName() {
        return pathogenName;
    }

    /**
     * Sets the pathogen or disease name.
     *
     * @param pathogenName the pathogen name
     */
    public void setPathogenName(String pathogenName) {
        this.pathogenName = pathogenName;
    }

    /**
     * Returns the affected animal species.
     *
     * @return the affected animal species
     */
    public String getAnimalSpeciesAffected() {
        return animalSpeciesAffected;
    }

    /**
     * Sets the affected animal species.
     *
     * @param animalSpeciesAffected the affected animal species
     */
    public void setAnimalSpeciesAffected(String animalSpeciesAffected) {
        this.animalSpeciesAffected = animalSpeciesAffected;
    }

    /**
     * Returns the confirmed human case count.
     *
     * @return the confirmed human case count
     */
    public Long getConfirmedHumanCases() {
        return confirmedHumanCases;
    }

    /**
     * Sets the confirmed human case count.
     *
     * @param confirmedHumanCases the confirmed human case count
     */
    public void setConfirmedHumanCases(Long confirmedHumanCases) {
        this.confirmedHumanCases = confirmedHumanCases;
    }

    /**
     * Returns the confirmed animal case count.
     *
     * @return the confirmed animal case count
     */
    public Long getConfirmedAnimalCases() {
        return confirmedAnimalCases;
    }

    /**
     * Sets the confirmed animal case count.
     *
     * @param confirmedAnimalCases the confirmed animal case count
     */
    public void setConfirmedAnimalCases(Long confirmedAnimalCases) {
        this.confirmedAnimalCases = confirmedAnimalCases;
    }

    /**
     * Returns the event classification.
     *
     * @return the event classification
     */
    public EventClassification getEventClassification() {
        return eventClassification;
    }

    /**
     * Sets the event classification.
     *
     * @param eventClassification the event classification
     */
    public void setEventClassification(EventClassification eventClassification) {
        this.eventClassification = eventClassification;
    }
}
