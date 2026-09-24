package zw.ac.uz.dpmds.zoonotic;

import org.junit.jupiter.api.Test;
import zw.ac.uz.dpmds.zoonotic.entity.EventClassification;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.Severity;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import zw.ac.uz.dpmds.zoonotic.service.ZoonoticAlertIntegrationService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the project-defined zoonotic alert qualification policy.
 *
 * @author DPDMS
 * @version 1.0
 */
class ZoonoticAlertIntegrationServiceTest {

    private final ZoonoticAlertIntegrationService service =
            new ZoonoticAlertIntegrationService("http://localhost:8082");

    /**
     * Ensures only approved incidents can qualify.
     */
    @Test
    void nonApprovedIncidentDoesNotQualify() {
        ZoonoticIncident incident = incident(
                IncidentStatus.PENDING,
                Severity.CRITICAL,
                EventClassification.OUTBREAK,
                1);

        assertFalse(service.isQualifyingApprovedIncident(incident));
    }

    /**
     * Ensures an outbreak qualifies after approval.
     */
    @Test
    void approvedOutbreakQualifies() {
        ZoonoticIncident incident = incident(
                IncidentStatus.APPROVED,
                Severity.LOW,
                EventClassification.OUTBREAK,
                0);

        assertTrue(service.isQualifyingApprovedIncident(incident));
    }

    /**
     * Ensures critical severity qualifies after approval.
     */
    @Test
    void approvedCriticalIncidentQualifies() {
        ZoonoticIncident incident = incident(
                IncidentStatus.APPROVED,
                Severity.CRITICAL,
                EventClassification.CLUSTER,
                0);

        assertTrue(service.isQualifyingApprovedIncident(incident));
    }

    /**
     * Ensures confirmed human cases qualify after approval.
     */
    @Test
    void approvedHumanCasesQualify() {
        ZoonoticIncident incident = incident(
                IncidentStatus.APPROVED,
                Severity.LOW,
                EventClassification.CLUSTER,
                1);

        assertTrue(service.isQualifyingApprovedIncident(incident));
    }

    /**
     * Ensures an approved incident matching no criterion does not qualify.
     */
    @Test
    void approvedIncidentWithNoCriterionDoesNotQualify() {
        ZoonoticIncident incident = incident(
                IncidentStatus.APPROVED,
                Severity.LOW,
                EventClassification.CLUSTER,
                0);

        assertFalse(service.isQualifyingApprovedIncident(incident));
    }

    private ZoonoticIncident incident(
            IncidentStatus status,
            Severity severity,
            EventClassification classification,
            long humanCases) {
        ZoonoticIncident incident = new ZoonoticIncident();
        incident.setStatus(status);
        incident.setSeverity(severity);
        incident.setEventClassification(classification);
        incident.setConfirmedHumanCases(humanCases);
        return incident;
    }
}
