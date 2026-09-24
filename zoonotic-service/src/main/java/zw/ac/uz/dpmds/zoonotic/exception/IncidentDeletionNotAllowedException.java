package zw.ac.uz.dpmds.zoonotic.exception;

/**
 * Indicates that an incident is outside the project deletion workflow.
 *
 * <p>The project design permits recorder deletion only while an incident is
 * pending. This preserves incidents that have entered review or correction
 * workflow for historical and audit purposes.</p>
 *
 * @author DPDMS
 * @version 1.0
 */
public class IncidentDeletionNotAllowedException extends RuntimeException {

    /**
     * Creates a pending-only deletion error.
     */
    public IncidentDeletionNotAllowedException() {
        super("Only pending incidents may be deleted.");
    }
}
