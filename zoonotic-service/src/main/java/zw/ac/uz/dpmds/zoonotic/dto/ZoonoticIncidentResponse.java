package zw.ac.uz.dpmds.zoonotic.dto;

import zw.ac.uz.dpmds.zoonotic.entity.EventClassification;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.Severity;

import java.time.LocalDateTime;

/**
 * Carries the API representation of a zoonotic incident.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class ZoonoticIncidentResponse {

    private Long id;
    private String ward;
    private String district;
    private String province;
    private LocalDateTime occurrenceDateTime;
    private String reporter;
    private Severity severity;
    private IncidentStatus status;
    private Double latitude;
    private Double longitude;
    private String pathogenName;
    private String animalSpeciesAffected;
    private Long confirmedHumanCases;
    private Long confirmedAnimalCases;
    private EventClassification eventClassification;

    /**
     * Returns the incident identifier.
     *
     * @return the incident identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the incident identifier.
     *
     * @param id the incident identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the ward.
     *
     * @return the ward
     */
    public String getWard() {
        return ward;
    }

    /**
     * Sets the ward.
     *
     * @param ward the ward
     */
    public void setWard(String ward) {
        this.ward = ward;
    }

    /**
     * Returns the district.
     *
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Sets the district.
     *
     * @param district the district
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * Returns the province.
     *
     * @return the province
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the province.
     *
     * @param province the province
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Returns the occurrence date and time.
     *
     * @return the occurrence date and time
     */
    public LocalDateTime getOccurrenceDateTime() {
        return occurrenceDateTime;
    }

    /**
     * Sets the occurrence date and time.
     *
     * @param occurrenceDateTime the occurrence date and time
     */
    public void setOccurrenceDateTime(LocalDateTime occurrenceDateTime) {
        this.occurrenceDateTime = occurrenceDateTime;
    }

    /**
     * Returns the reporter.
     *
     * @return the reporter
     */
    public String getReporter() {
        return reporter;
    }

    /**
     * Sets the reporter.
     *
     * @param reporter the reporter
     */
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    /**
     * Returns the severity.
     *
     * @return the severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity the severity
     */
    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    /**
     * Returns the workflow status.
     *
     * @return the status
     */
    public IncidentStatus getStatus() {
        return status;
    }

    /**
     * Sets the workflow status.
     *
     * @param status the status
     */
    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    /**
     * Returns the latitude.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Returns the longitude.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns the pathogen name.
     *
     * @return the pathogen name
     */
    public String getPathogenName() {
        return pathogenName;
    }

    /**
     * Sets the pathogen name.
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
