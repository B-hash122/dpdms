package com.dpdms.flood.exception;

/** Thrown whenever a caller's (ward, hazard) or role scope doesn't permit the action. */
public class HazardScopeViolationException extends RuntimeException {
    public HazardScopeViolationException(String message) {
        super(message);
    }
}
