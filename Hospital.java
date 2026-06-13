import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import java.io.Serializable;

/**
 * Business class representing a hospital in the emergency dispatch system.
 *
 * A hospital contains all information required for patient assignment
 * and emergency service management:
 * <ul>
 *     <li>a unique identifier;</li>
 *     <li>a display name;</li>
 *     <li>a geographic position;</li>
 *     <li>a maximum capacity;</li>
 *     <li>a current occupancy level;</li>
 *     <li>the medical services available.</li>
 * </ul>
 *
 * This class implements {@link Serializable} so that hospitals can be
 * saved and restored using the persistence layer of the application.
 *
 * Two hospitals are considered equal when they share the same identifier.
 *
 * <p><b>Class type:</b> Business class (Model layer).</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class Hospital implements Serializable {

    /**
     * Unique hospital identifier.
     */
    private String id;

    /**
     * Hospital display name.
     */
    private String name;

    /**
     * Geographic position of the hospital.
     */
    private Coordinate position;

    /**
     * Maximum number of patients that can be admitted.
     */
    private int maxCapacity;

    /**
     * Current number of admitted patients.
     */
    private int currentCapacity;

    /**
     * Set of medical services provided by the hospital.
     */
    private Set<HospitalServiceType> services;

    /**
     * Serialization identifier.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new hospital.
     *
     * The hospital is initially empty and no medical service
     * is registered by default.
     *
     * @param id unique hospital identifier
     * @param name hospital name
     * @param position hospital geographic position
     * @param maxCapacity maximum number of patients that can be admitted
     */
    public Hospital(
            String id,
            String name,
            Coordinate position,
            int maxCapacity) {

        this.id = id;
        this.name = name;
        this.position = position;
        this.maxCapacity = maxCapacity;

        // A newly created hospital starts with no admitted patients.
        this.currentCapacity = 0;

        // Services are added later depending on hospital specialization.
        this.services = new HashSet<>();
    }

    /**
     * Computes the hospital saturation rate.
     *
     * The returned value is between 0 and 1 and is used
     * for statistics, patient assignment visualization
     * and hospital status monitoring.
     *
     * @return saturation rate of the hospital
     */
    public double getSaturationRate() {
        return (double) currentCapacity / maxCapacity;
    }

    /**
     * Checks whether the hospital has reached its maximum capacity.
     *
     * @return true if the hospital is saturated
     */
    public boolean isSaturated() {
        return currentCapacity >= maxCapacity;
    }

    /**
     * Updates the current occupancy level.
     *
     * The value is automatically clamped between 0 and the
     * maximum capacity in order to preserve object consistency.
     *
     * @param currentCapacity new occupancy level
     */
    public void updateCapacity(int currentCapacity) {

        if (currentCapacity < 0) {
            this.currentCapacity = 0;
            return;
        }

        if (currentCapacity > maxCapacity) {
            this.currentCapacity = maxCapacity;
            return;
        }

        this.currentCapacity = currentCapacity;
    }

    /**
     * Adds a medical service to the hospital.
     *
     * A Set is used to automatically prevent duplicates.
     *
     * @param service medical service to add
     */
    public void addService(HospitalServiceType service) {
        services.add(service);
    }

    /**
     * Checks whether the hospital provides a specific service.
     *
     * This method is mainly used by AssignmentService when
     * searching for the most appropriate hospital for a patient.
     *
     * @param service requested medical service
     * @return true if the service is available
     */
    public boolean hasService(HospitalServiceType service) {
        return services.contains(service);
    }

    /**
     * Returns the hospital identifier.
     *
     * @return hospital identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the hospital name.
     *
     * @return hospital name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the hospital position.
     *
     * @return geographic position
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * Returns the maximum capacity.
     *
     * @return maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Returns the current occupancy level.
     *
     * @return current number of admitted patients
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    /**
     * Returns a defensive copy of the service set.
     *
     * Returning a copy prevents external classes from
     * modifying the internal collection directly.
     *
     * @return copy of available services
     */
    public Set<HospitalServiceType> getServices() {
        return new HashSet<>(services);
    }

    /**
     * Updates the geographic position of the hospital.
     *
     * This method is notably used when a hospital is moved
     * through the graphical interface.
     *
     * @param newPosition new hospital position
     */
    public void updatePosition(Coordinate newPosition) {
        this.position = newPosition;
    }

    /**
     * Attempts to admit a new patient.
     *
     * The occupancy level is increased only if the hospital
     * is not already saturated.
     *
     * @return true if the patient was successfully admitted
     */
    public boolean admitPatient() {

        if (isSaturated()) {
            return false;
        }

        currentCapacity++;
        return true;
    }

    /**
     * Returns a complete textual representation of the hospital.
     *
     * Mainly used for debugging and console outputs.
     *
     * @return hospital description
     */
    @Override
    public String toString() {
        return "Hospital{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", maxCapacity=" + maxCapacity +
                ", currentCapacity=" + currentCapacity +
                ", services=" + services +
                '}';
    }

    /**
     * Compares this hospital with another object.
     *
     * Two hospitals are considered equal when they share
     * the same unique identifier.
     *
     * @param obj object to compare
     * @return true if both hospitals have the same identifier
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Hospital)) {
            return false;
        }

        Hospital other = (Hospital) obj;

        return Objects.equals(id, other.id);
    }

    /**
     * Generates a hash code consistent with equals().
     *
     * This implementation allows Hospital objects to be
     * safely stored in hash-based collections such as
     * HashSet and HashMap.
     *
     * @return hospital hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}