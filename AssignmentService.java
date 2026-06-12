import java.util.List;

public class AssignmentService {

    // Static car la méthode ne dépend d'aucun attribut de la classe.
    public static Hospital findBestHospital(
            Patient patient,
            List<Hospital> hospitals
    ) {
        Hospital bestHospital = null;
        double bestDistance = Double.MAX_VALUE;

        for (Hospital hospital : hospitals) {

            // On ignore les hôpitaux saturés
            if (hospital.isSaturated()) {
                continue;
            }

            // On ignore les hôpitaux qui n'ont pas le service demandé
            if (!hospital.hasService(patient.getRequiredService())) {
                continue;
            }

            double distance =
                    patient.getPosition()
                            .distanceTo(hospital.getPosition());

            if (distance < bestDistance) {
                bestDistance = distance;
                bestHospital = hospital;
            }
        }

        return bestHospital;
    }
}