package zw.ac.uz.dpmds.zoonotic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncidentAudit;

import java.util.List;

/**
 * Provides persistence operations for zoonotic incident audit records.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public interface ZoonoticIncidentAuditRepository
        extends JpaRepository<ZoonoticIncidentAudit, Long> {

    /**
     * Finds audit records for an incident in chronological order.
     *
     * @param incidentId the incident identifier
     * @return chronologically ordered audit records
     */
    List<ZoonoticIncidentAudit> findByIncidentIdOrderByPerformedAtAsc(Long incidentId);
}
