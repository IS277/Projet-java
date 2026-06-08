import java.util.ArrayList;
import java.util.List;

public class Main {

        public static void main(String[] args) {

                Hospital h1 = new Hospital(
                                "H1",
                                "Paris Hospital",
                                new Coordinate(10, 20),
                                100);
                h1.addService(HospitalServiceType.NEUROLOGY);

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

                Hospital h4 = new Hospital(
                                "H4",
                                "West Hospital",
                                new Coordinate(30, 150),
                                70);
                h4.addService(HospitalServiceType.GENERAL);

                Hospital h5 = new Hospital(
                                "H5",
                                "South Hospital",
                                new Coordinate(150, 80),
                                90);
                h5.addService(HospitalServiceType.CARDIOLOGY);

                Patient patient = new Patient(
                                "P1",
                                "Alice",
                                new Coordinate(15, 25),
                                HospitalServiceType.NEUROLOGY);

                MapManager manager = new MapManager();

                manager.addHospital(h1);
                manager.addHospital(h2);
                manager.addHospital(h3);
                manager.addHospital(h4);
                manager.addHospital(h5);

                List<Hospital> hospitals = new ArrayList<>(
                                manager.getHospitals().values());

                AssignmentService service = new AssignmentService();

                Hospital result = service.findBestHospital(
                                patient,
                                hospitals);

                System.out.println(
                                "Patient : "
                                                + patient.getName());

                System.out.println(
                                "Required service : "
                                                + patient.getRequiredService());

                System.out.println(
                                "Best hospital : "
                                                + result.getName());

                List<DelaunayTriangle> triangles = manager.getTriangles();

                System.out.println();
                System.out.println("Delaunay triangles:");

                for (DelaunayTriangle triangle : triangles) {
                        System.out.println(triangle);
                }

                System.out.println();
                System.out.println("ASCII map:");

                AsciiMapRenderer.drawHospitalsAndDelaunay(
                                hospitals,
                                triangles,
                                40,
                                20);

                VoronoiService voronoi = new VoronoiService();

                List<Coordinate> vertices = voronoi.getVoronoiVertices(
                                triangles);

                System.out.println();
                System.out.println("Voronoi vertices:");

                for (Coordinate vertex : vertices) {
                        System.out.println(vertex);
                }

                List<VoronoiEdge> voronoiEdges = voronoi.getVoronoiEdges(
                                triangles);

                System.out.println();
                System.out.println("Voronoi edges:");

                for (VoronoiEdge edge : voronoiEdges) {
                        System.out.println(edge);
                }
                List<VoronoiZone> zones = manager.getZones();

                System.out.println();
                System.out.println("Voronoi zones:");

                for (VoronoiZone zone : zones) {
                        System.out.println(zone);
                }

                System.out.println();
                System.out.println("=== Moving H1 ===");

                manager.moveHospital(
                                "H1",
                                new Coordinate(120, 120));

                System.out.println();

                for (DelaunayTriangle triangle : manager.getTriangles()) {
                        System.out.println(triangle);
                }

                System.out.println();
                System.out.println("New Voronoi zones:");

                for (VoronoiZone zone : manager.getZones()) {
                        System.out.println(zone);
                }
        }

}