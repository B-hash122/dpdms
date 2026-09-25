package com.dpdms.flood.repository;

import com.dpdms.flood.entity.FloodIncident;
import com.dpdms.flood.enums.IncidentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FloodRepository extends JpaRepository<FloodIncident, Long> {

    // Used by the dashboard/report-service: only APPROVED incidents may be
    // shown on the dashboard, the map, or in generated reports.
    List<FloodIncident> findByStatus(IncidentStatus status);

    List<FloodIncident> findByWardAndStatus(String ward, IncidentStatus status);

    List<FloodIncident> findByDistrict(String district);

    List<FloodIncident> findByProvince(String province);
}
