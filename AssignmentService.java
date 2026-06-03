

import java.util.List;

public class AssignmentService {

   
    public Hospital findBestHospital(
            Patient patient,
            List<Hospital> hospitals
    ) {

        Hospital bestHospital = null;

        double bestDistance = Double.MAX_VALUE;

        for (Hospital hospital : hospitals) {

            
            if (hospital.isSaturated()) {
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
