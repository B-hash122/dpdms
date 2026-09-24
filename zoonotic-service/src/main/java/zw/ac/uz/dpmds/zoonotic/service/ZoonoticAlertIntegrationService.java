package zw.ac.uz.dpmds.zoonotic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import zw.ac.uz.dpmds.zoonotic.dto.ZoonoticAlertRequest;
import zw.ac.uz.dpmds.zoonotic.entity.EventClassification;
import zw.ac.uz.dpmds.zoonotic.entity.IncidentStatus;
import zw.ac.uz.dpmds.zoonotic.entity.Severity;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;

/**
 * Publishes qualifying approved incidents to the dedicated alert service.
 *
 * <p>{@link Async} provides asynchronous execution only; it is not a durable
 * message broker. A broker can replace this boundary in a later phase.</p>
 *
 * @author DPDMS
 * @version 1.0
 */
@Service
public class ZoonoticAlertIntegrationService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ZoonoticAlertIntegrationService.class);

    private final RestClient restClient;

    /**
     * Creates the integration client using the external alert-service URL.
     *
     * @param alertServiceUrl the configured alert-service base URL
     */
    public ZoonoticAlertIntegrationService(
            @Value("${alert.service.url}") String alertServiceUrl) {
        restClient = RestClient.builder()
                .baseUrl(alertServiceUrl)
                .build();
    }

    /**
     * Sends a qualifying approved incident without affecting approval.
     *
     * @param incident the persisted incident after a successful approval
     */
    @Async
    public void publishApprovedIncident(
            ZoonoticIncident incident,
            String bearerToken) {
        if (!isQualifyingApprovedIncident(incident)) {
            return;
        }

        try {
            restClient.post()
                    .uri("/api/alerts/zoonotic")
                    .headers(headers -> {
                        if (bearerToken != null) {
                            headers.setBearerAuth(bearerToken);
                        }
                    })
                    .body(new ZoonoticAlertRequest(incident))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOGGER.error(
                    "Alert-service integration failed for approved incident {}",
                    incident.getId(),
                    exception);
        }
    }

    /**
     * Applies the project-defined alerting policy.
     *
     * @param incident the incident under consideration
     * @return true when the approved incident must be sent for alerting
     */
    public boolean isQualifyingApprovedIncident(ZoonoticIncident incident) {
        if (incident == null || incident.getStatus() != IncidentStatus.APPROVED) {
            return false;
        }
        return incident.getEventClassification() == EventClassification.OUTBREAK
                || incident.getSeverity() == Severity.CRITICAL
                || incident.getConfirmedHumanCases() > 0;
    }

    /**
     * Reads the current request's JWT for authenticated service communication.
     *
     * @return the bearer token, or null when no JWT authentication is present
     */
    public String getCurrentBearerToken() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return jwtAuthentication.getToken().getTokenValue();
        }
        return null;
    }
}
