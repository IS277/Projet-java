import java.util.List;

/**
 * Classe service responsable de l'affectation des patients.
 *
 * Cette classe contient les algorithmes permettant
 * de sélectionner l'hôpital le plus adapté à un patient
 * selon différents critères.
 *
 * Dans cette version du projet, le choix est effectué
 * en fonction :
 * <ul>
 *     <li>de la disponibilité de l'hôpital ;</li>
 *     <li>du service médical demandé ;</li>
 *     <li>de la distance entre le patient et l'hôpital.</li>
 * </ul>
 *
 * @author Équipe Projet Emergency Dispatcher
 * @version 1.0
 */
public class AssignmentService {

    /**
     * Recherche l'hôpital le plus proche capable de prendre
     * en charge un patient.
     *
     * Les hôpitaux saturés sont ignorés.
     * Les hôpitaux ne proposant pas le service demandé
     * sont également exclus de la recherche.
     *
     * Si aucun établissement ne satisfait les contraintes,
     * la méthode retourne {@code null}.
     *
     * @param patient patient à affecter
     * @param hospitals liste des hôpitaux disponibles
     * @return l'hôpital sélectionné ou null si aucun hôpital
     *         ne peut accueillir le patient
     */
    public static Hospital findBestHospital(
            Patient patient,
            List<Hospital> hospitals
    ) {

        Hospital bestHospital = null;
        double bestDistance = Double.MAX_VALUE;

        for (Hospital hospital : hospitals) {

            // Un hôpital plein ne peut pas accueillir de nouveau patient.
            if (hospital.isSaturated()) {
                continue;
            }

            // Le service médical demandé doit être disponible.
            if (!hospital.hasService(patient.getRequiredService())) {
                continue;
            }

            double distance =
                    patient.getPosition()
                            .distanceTo(hospital.getPosition());

            // On conserve uniquement l'hôpital le plus proche.
            if (distance < bestDistance) {
                bestDistance = distance;
                bestHospital = hospital;
            }
        }

        return bestHospital;
    }
}