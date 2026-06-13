import java.util.List;

/**
 * Service class responsible for patient assignment.
 *
 * This class contains the algorithms used to determine
 * the most appropriate hospital for a patient according
 * to several criteria.
 *
 * In the current implementation, hospital selection is based on:
 * <ul>
 *     <li>hospital availability;</li>
 *     <li>availability of the required medical service;</li>
 *     <li>distance between the patient and the hospital.</li>
 * </ul>
 *
 * The objective is to assign each patient to the nearest
 * hospital capable of providing the requested care while
 * respecting capacity constraints.
 *
 * <p><b>Class type:</b> Service class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class AssignmentService {

    /**
     * Finds the most suitable hospital for a patient.
     *
     * The method iterates through all available hospitals
     * and applies the following filters:
     * <ol>
     *     <li>the hospital must not be saturated;</li>
     *     <li>the hospital must provide the required service;</li>
     *     <li>among all valid hospitals, the closest one is selected.</li>
     * </ol>
     *
     * If no hospital satisfies these constraints,
     * the method returns {@code null}.
     *
     * @param patient patient requiring medical assistance
     * @param hospitals list of available hospitals
     * @return the closest compatible hospital,
     *         or {@code null} if no suitable hospital exists
     */
    public static Hospital findBestHospital(
            Patient patient,
            List<Hospital> hospitals
    ) {

        Hospital bestHospital = null;

        // Start with the largest possible distance.
        double bestDistance = Double.MAX_VALUE;

        for (Hospital hospital : hospitals) {

            // A saturated hospital cannot accept additional patients.
            if (hospital.isSaturated()) {
                continue;
            }

            // The hospital must provide the medical service requested
            // by the patient.
            if (!hospital.hasService(patient.getRequiredService())) {
                continue;
            }

            double distance =
                    patient.getPosition()
                            .distanceTo(hospital.getPosition());

            // Keep only the closest valid hospital found so far.
            if (distance < bestDistance) {
                bestDistance = distance;
                bestHospital = hospital;
            }
        }

        return bestHospital;
    }
}