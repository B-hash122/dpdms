package zw.ac.uz.dpmds.zoonotic.exception;

import java.time.LocalDateTime;

/**
 * Represents a structured error returned by the REST API.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class RestErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;

    /**
     * Creates an error response.
     *
     * @param timestamp the error timestamp
     * @param status the HTTP status
     * @param error the HTTP error name
     * @param message the error message
     */
    public RestErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * Returns the error timestamp.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the HTTP status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the HTTP error name.
     *
     * @return the error name
     */
    public String getError() {
        return error;
    }

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }
}
