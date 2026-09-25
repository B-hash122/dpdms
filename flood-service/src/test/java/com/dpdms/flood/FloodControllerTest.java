package com.dpdms.flood;

import com.dpdms.flood.controller.FloodController;
import com.dpdms.flood.dto.ApprovalActionRequest;
import com.dpdms.flood.dto.FloodIncidentRequest;
import com.dpdms.flood.entity.FloodIncident;
import com.dpdms.flood.enums.IncidentStatus;
import com.dpdms.flood.enums.Severity;
import com.dpdms.flood.exception.HazardScopeViolationException;
import com.dpdms.flood.repository.FloodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Covers the two rule families the case study calls out explicitly as
 * the most common source of bugs: hazard/ward scoping, and the
 * PENDING -> APPROVED/REJECTED/CORRECTIONS_REQUESTED workflow.
 */
class FloodControllerTest {

    private FloodRepository repository;
    private FloodController controller;

    @BeforeEach
    void setUp() {
        repository = mock(FloodRepository.class);
        controller = new FloodController(repository);
        when(repository.save(any(FloodIncident.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private FloodIncidentRequest sampleRequest(String ward) {
        FloodIncidentRequest req = new FloodIncidentRequest();
        req.setWard(ward);
        req.setDistrict("Rushinga");
        req.setProvince("Mashonaland Central");
        req.setDateTime(LocalDateTime.now());
        req.setReporter("Fidelis");
        req.setSeverity(Severity.HIGH);
        req.setLatitude(-16.7);
        req.setLongitude(32.1);
        req.setPeakWaterLevelMetres(3.2);
        req.setRiverBasin("Mazowe");
        req.setHouseholdsDisplaced(40);
        req.setAreaFloodedHectares(12.5);
        req.setDurationDays(3);
        return req;
    }

    // ---------- ward scoping ----------

    @Test
    void recorder_canCreateIncidentInOwnWard() {
        ResponseEntity<FloodIncident> response =
                controller.create(sampleRequest("Ward 12"), "FLOOD_RECORDER", "Ward 12");

        assertEquals(201, response.getStatusCode().value());
        assertEquals(IncidentStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void recorder_cannotCreateIncidentInAnotherWard() {
        assertThrows(HazardScopeViolationException.class,
                () -> controller.create(sampleRequest("Ward 99"), "FLOOD_RECORDER", "Ward 12"));
    }

    @Test
    void recorder_withoutWardHeader_isRejectedOnList() {
        assertThrows(HazardScopeViolationException.class,
                () -> controller.getAll("FLOOD_RECORDER", null, null));
    }

    @Test
    void droughtSupervisor_cannotActOnFloodService() {
        // Simulates a supervisor for a *different* hazard trying to hit /api/v1/floods.
        assertThrows(HazardScopeViolationException.class,
                () -> controller.approve(1L, new ApprovalActionRequest(), "DROUGHT_SUPERVISOR"));
    }

    @Test
    void nationalUser_cannotCreateOrApprove() {
        assertThrows(HazardScopeViolationException.class,
                () -> controller.create(sampleRequest("Ward 12"), "NATIONAL", null));
        assertThrows(HazardScopeViolationException.class,
                () -> controller.approve(1L, new ApprovalActionRequest(), "NATIONAL"));
    }

    // ---------- approval workflow ----------

    @Test
    void newIncident_defaultsToPending() {
        ResponseEntity<FloodIncident> response =
                controller.create(sampleRequest("Ward 12"), "FLOOD_RECORDER", "Ward 12");
        assertEquals(IncidentStatus.PENDING, response.getBody().getStatus());
    }

    @Test
    void supervisor_canApprovePendingIncident() {
        FloodIncident pending = FloodIncident.builder().id(1L).ward("Ward 12").status(IncidentStatus.PENDING).build();
        when(repository.findById(1L)).thenReturn(Optional.of(pending));

        ResponseEntity<FloodIncident> response =
                controller.approve(1L, new ApprovalActionRequest(), "FLOOD_SUPERVISOR");

        assertEquals(IncidentStatus.APPROVED, response.getBody().getStatus());
    }

    @Test
    void supervisor_mustGiveReasonToReject() {
        FloodIncident pending = FloodIncident.builder().id(1L).ward("Ward 12").status(IncidentStatus.PENDING).build();
        when(repository.findById(1L)).thenReturn(Optional.of(pending));

        ApprovalActionRequest noReason = new ApprovalActionRequest();
        assertThrows(IllegalArgumentException.class,
                () -> controller.reject(1L, noReason, "FLOOD_SUPERVISOR"));
    }

    @Test
    void recorder_cannotEditIncidentOnceApproved() {
        FloodIncident approved = FloodIncident.builder().id(1L).ward("Ward 12").status(IncidentStatus.APPROVED).build();
        when(repository.findById(1L)).thenReturn(Optional.of(approved));

        assertThrows(HazardScopeViolationException.class,
                () -> controller.update(1L, sampleRequest("Ward 12"), "FLOOD_RECORDER", "Ward 12"));
    }

    @Test
    void correctionsRequested_goesBackToPendingOnResubmission() {
        FloodIncident needsCorrection = FloodIncident.builder()
                .id(1L).ward("Ward 12").status(IncidentStatus.CORRECTIONS_REQUESTED).build();
        when(repository.findById(1L)).thenReturn(Optional.of(needsCorrection));

        ResponseEntity<FloodIncident> response =
                controller.update(1L, sampleRequest("Ward 12"), "FLOOD_RECORDER", "Ward 12");

        assertEquals(IncidentStatus.PENDING, response.getBody().getStatus());
    }
}
