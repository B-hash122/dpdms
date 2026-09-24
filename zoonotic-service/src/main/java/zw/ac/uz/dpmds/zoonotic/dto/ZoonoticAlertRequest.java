package zw.ac.uz.dpmds.zoonotic.dto;

import java.time.LocalDateTime;

import zw.ac.uz.dpmds.zoonotic.entity.EventClassification;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.Severity;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.security.HazardTypes;

/**
 * Carries an approved zoonotic incident to the dedicated alert service.
 *
 * @author DPDMS
 * @version 1.0
 */
public class ZoonoticAlertRequest {

    private Long incidentId;
    private String hazardType;
    private String pathogenName;
    private String animalSpecies;
    private String ward;
    private String district;
    private String province;
    private LocalDateTime occurrenceDateTime;
    private Severity severity;
    private EventClassification eventClassification;
    private Long confirmedHumanCases;
    private Long confirmedAnimalCases;
    private IncidentStatus status;

    /**
     * Creates an empty request for JSON serialization.
     */
    public ZoonoticAlertRequest() {
    }

    /**
     * Creates an alert request from a persisted incident.
     *
     * @param incident the approved incident
     */
    public ZoonoticAlertRequest(ZoonoticIncident incident) {
        incidentId = incident.getId();
        hazardType = HazardTypes.ZOONOTIC;
        pathogenName = incident.getPathogenName();
        animalSpecies = incident.getAnimalSpeciesAffected();
        ward = incident.getWard();
        district = incident.getDistrict();
        province = incident.getProvince();
        occurrenceDateTime = incident.getOccurrenceDateTime();
        severity = incident.getSeverity();
        eventClassification = incident.getEventClassification();
        confirmedHumanCases = incident.getConfirmedHumanCases();
        confirmedAnimalCases = incident.getConfirmedAnimalCases();
        status = incident.getStatus();
    }

    public Long getIncidentId() {
        return incidentId;
    }

    public String getHazardType() {
        return hazardType;
    }

    public String getPathogenName() {
        return pathogenName;
    }

    /**
     * Returns the affected animal species.
     *
     * @return the affected animal species
     */
    public String getAnimalSpecies() {
        return animalSpecies;
    }

    public String getWard() {
        return ward;
    }

    public String getDistrict() {
        return district;
    }

    public String getProvince() {
        return province;
    }

    public LocalDateTime getOccurrenceDateTime() {
        return occurrenceDateTime;
    }

    public Severity getSeverity() {
        return severity;
    }

    public EventClassification getEventClassification() {
        return eventClassification;
    }

    public Long getConfirmedHumanCases() {
        return confirmedHumanCases;
    }

    public Long getConfirmedAnimalCases() {
        return confirmedAnimalCases;
    }

    public IncidentStatus getStatus() {
        return status;
    }
}
