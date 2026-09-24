package zw.ac.uz.dpmds.zoonotic.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Centralizes REST error responses for the zoonotic incident API.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * Handles missing incident errors.
     *
     * @param exception the missing incident exception
     * @return a not-found error response
     */
    @ExceptionHandler(ZoonoticIncidentNotFoundException.class)
    public ResponseEntity<RestErrorResponse> handleNotFound(
            ZoonoticIncidentNotFoundException exception) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage());
    }

    /**
     * Handles invalid incident workflow transitions.
     *
     * @param exception the invalid transition exception
     * @return a conflict error response
     */
    @ExceptionHandler(InvalidIncidentStatusTransitionException.class)
    public ResponseEntity<RestErrorResponse> handleInvalidTransition(
            InvalidIncidentStatusTransitionException exception) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage());
    }

    /**
     * Handles deletion attempts against a protected workflow state.
     *
     * @param exception the deletion workflow exception
     * @return a conflict error response
     */
    @ExceptionHandler(IncidentDeletionNotAllowedException.class)
    public ResponseEntity<RestErrorResponse> handleDeletionNotAllowed(
            IncidentDeletionNotAllowedException exception) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage());
    }

    /**
     * Handles authenticated users outside their assigned incident scope.
     *
     * @param exception the scope exception
     * @return a forbidden error response
     */
    @ExceptionHandler(IncidentScopeAccessDeniedException.class)
    public ResponseEntity<RestErrorResponse> handleScopeDenied(
            IncidentScopeAccessDeniedException exception) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    /**
     * Handles request body validation errors.
     *
     * @param exception the validation exception
     * @return a bad-request error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorResponse> handleValidation(
            MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles validation constraint errors raised outside request binding.
     *
     * @param exception the constraint violation exception
     * @return a bad-request error response
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * Builds a consistent structured REST error response.
     *
     * @param status the HTTP status
     * @param message the error message
     * @return the structured error response
     */
    private ResponseEntity<RestErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message) {
        RestErrorResponse response = new RestErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message);
        return ResponseEntity.status(status).body(response);
    }
}
