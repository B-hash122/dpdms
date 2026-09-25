package com.dpdms.flood.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Body for the supervisor-only approval endpoints
 * (POST /api/v1/floods/{id}/approve|reject|request-corrections).
 * `reason` is required for reject and request-corrections, optional
 * for approve.
 */
@Getter
@Setter
public class ApprovalActionRequest {
    private String reason;
    private String actedBy; // supervisor's username/id, until auth-service issues a security context
}
