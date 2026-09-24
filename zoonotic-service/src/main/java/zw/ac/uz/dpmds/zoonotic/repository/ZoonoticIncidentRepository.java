package zw.ac.uz.dpmds.zoonotic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.ac.uz.dpmds.zoonotic.entity.ZoonoticIncident;
import java.util.List;

/**
 * Provides persistence operations for zoonotic incidents.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public interface ZoonoticIncidentRepository extends JpaRepository<ZoonoticIncident, Long> {

    /**
     * Finds incidents assigned to one ward.
     *
     * @param ward the authorized ward
     * @return incidents in the ward
     */
    List<ZoonoticIncident> findByWard(String ward);
}
