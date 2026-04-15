package healthsystem.model;

/**
 * Alertable interface — demonstrates abstraction via interfaces.
 * Any class that generates health alerts must implement this.
 */
public interface Alertable {

    /** Returns an alert message if a health concern is detected or null if everything is normal. */
    String generateAlert();

    /** Returns true if the current readings are within safe bounds
     */
    boolean isHealthy();
}
