/**
 * Business class representing a patient in the emergency dispatch system.
 *
 * A patient is characterized by an identity, a geographic position and a
 * required medical service. The required service is used during the assignment
 * process to determine which hospitals are able to provide the appropriate
 * care.
 *
 * This class extends {@link Person} in order to reuse the common attributes
 * shared by all people in the system, such as the identifier, name and
 * geographic position.
 *
 * <p><b>Class type:</b> Business / Model class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class Patient extends Person {

    /**
     * Medical service required by the patient.
     */
    private HospitalServiceType requiredService;

    /**
     * Creates a new patient.
     *
     * @param id unique patient identifier
     * @param name patient name
     * @param position geographic position of the patient
     * @param requiredService medical service required by the patient
     */
    public Patient(
            String id,
            String name,
            Coordinate position,
            HospitalServiceType requiredService
    ) {
        super(id, name, position);
        this.requiredService = requiredService;
    }

    /**
     * Returns the medical service required by the patient.
     *
     * This information is used by {@link AssignmentService} when searching
     * for the most suitable hospital. Hospitals that do not provide this
     * service are automatically excluded from the selection process.
     *
     * @return required medical service
     */
    public HospitalServiceType getRequiredService() {
        return requiredService;
    }

    /**
     * Returns a textual representation of the patient.
     *
     * This method is mainly useful for debugging, logging and console-based
     * displays during testing.
     *
     * @return complete patient description
     */
    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", requiredService=" + requiredService +
                '}';
    }
}