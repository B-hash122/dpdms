package zw.ac.uz.dpmds.zoonotic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Stores the shared metadata and zoonotic indicators for a disease incident.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@Entity
@Table(name = "zoonotic_incidents")
public class ZoonoticIncident {

    private static final long MIN_CASE_COUNT = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String ward;

    @NotBlank
    @Column(nullable = false)
    private String district;

    @NotBlank
    @Column(nullable = false)
    private String province;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime occurrenceDateTime;

    @NotBlank
    @Column(nullable = false)
    private String reporter;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status = IncidentStatus.PENDING;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    @Column(nullable = false)
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    @Column(nullable = false)
    private Double longitude;

    @NotBlank
    @Column(nullable = false)
    private String pathogenName;

    @NotBlank
    @Column(nullable = false)
    private String animalSpeciesAffected;

    @Min(MIN_CASE_COUNT)
    @Column(nullable = false)
    private long confirmedHumanCases;

    @Min(MIN_CASE_COUNT)
    @Column(nullable = false)
    private long confirmedAnimalCases;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventClassification eventClassification;

    /**
     * Creates an empty incident for JPA.
     */
    public ZoonoticIncident() {
    }

    /**
     * Returns the generated database identifier.
     *
     * @return the incident identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the database identifier.
     *
     * @param id the incident identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

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
     * Returns the workflow status.
     *
     * @return the incident status
     */
    public IncidentStatus getStatus() {
        return status;
    }

    /**
     * Sets the workflow status.
     *
     * @param status the incident status
     */
    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    /**
     * Returns the latitude of the incident.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the incident.
     *
     * @param latitude the latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude of the incident.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the incident.
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
     * Returns the number of confirmed human cases.
     *
     * @return the confirmed human case count
     */
    public long getConfirmedHumanCases() {
        return confirmedHumanCases;
    }

    /**
     * Sets the number of confirmed human cases.
     *
     * @param confirmedHumanCases the confirmed human case count
     */
    public void setConfirmedHumanCases(long confirmedHumanCases) {
        this.confirmedHumanCases = confirmedHumanCases;
    }

    /**
     * Returns the number of confirmed animal cases.
     *
     * @return the confirmed animal case count
     */
    public long getConfirmedAnimalCases() {
        return confirmedAnimalCases;
    }

    /**
     * Sets the number of confirmed animal cases.
     *
     * @param confirmedAnimalCases the confirmed animal case count
     */
    public void setConfirmedAnimalCases(long confirmedAnimalCases) {
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
