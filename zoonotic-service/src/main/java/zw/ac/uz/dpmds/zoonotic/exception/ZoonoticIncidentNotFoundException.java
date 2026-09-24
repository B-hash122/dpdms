package zw.ac.uz.dpmds.zoonotic.exception;

/**
 * Indicates that a requested zoonotic incident does not exist.
 *
 * @author Bruce Bhomba
 * @version 1.0
 */
public class ZoonoticIncidentNotFoundException extends RuntimeException {

    /**
     * Creates an exception for a missing incident.
     *
     * @param id the missing incident identifier
     */
    public ZoonoticIncidentNotFoundException(Long id) {
        super("Zoonotic incident not found with id: " + id);
    }
}
