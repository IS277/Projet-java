import java.util.ArrayList;
import java.util.List;

public class Main {

        public static void main(String[] args) {

                // Création du premier hôpital
                Hospital h1 = new Hospital(
                                "H1",
                                "Paris Hospital",
                                new Coordinate(10, 20),
                                100);

                h1.addService(HospitalServiceType.NEUROLOGY);

                // Création du deuxième hôpital
                Hospital h2 = new Hospital(
                                "H2",
                                "City Hospital",
                                new Coordinate(100, 200),
                                50);

                h2.addService(HospitalServiceType.CARDIOLOGY);

                Hospital h3 = new Hospital(
                                "H3",
                                "North Hospital",
                                new Coordinate(80, 40),
                                80);

                h3.addService(HospitalServiceType.GENERAL);

                // Création du patient
                Patient patient = new Patient(
                                "P1",
                                "Alice",
                                new Coordinate(15, 25),
                                HospitalServiceType.NEUROLOGY);

                // Liste des hôpitaux
                List<Hospital> hospitals = new ArrayList<>();

                hospitals.add(h1);
                hospitals.add(h2);
                hospitals.add(h3);

                // Recherche du meilleur hôpital
                AssignmentService service = new AssignmentService();

                Hospital result = service.findBestHospital(
                                patient,
                                hospitals);

                // Affichage du résultat
                if (result != null) {

                        System.out.println(
                                        "Patient : "
                                                        + patient.getName());

                        System.out.println(
                                        "Required service : "
                                                        + patient.getRequiredService());

                        System.out.println(
                                        "Best hospital : "
                                                        + result.getName());

                } else {

                        System.out.println(
                                        "No suitable hospital found.");
                }

                Delaunay delaunay = new Delaunay();

                List<DelaunayTriangle> triangles = delaunay.triangulate(hospitals);

                System.out.println("Delaunay triangles:");

                for (DelaunayTriangle triangle : triangles) {
                        System.out.println(triangle);
                }
        }
}