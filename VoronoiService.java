import java.util.ArrayList;
import java.util.List;

public class VoronoiService {

    public List<Coordinate> getVoronoiVertices(List<DelaunayTriangle> triangles) {
        List<Coordinate> vertices = new ArrayList<>();

        for (DelaunayTriangle triangle : triangles) {
            vertices.add(triangle.getCircumcenter());
        }

        return vertices;
    }

    public List<VoronoiEdge> getVoronoiEdges(List<DelaunayTriangle> triangles) {
        List<VoronoiEdge> edges = new ArrayList<>();

        for (int i = 0; i < triangles.size(); i++) {
            for (int j = i + 1; j < triangles.size(); j++) {
                DelaunayTriangle t1 = triangles.get(i);
                DelaunayTriangle t2 = triangles.get(j);

                if (t1.sharesEdgeWith(t2)) {
                    Coordinate c1 = t1.getCircumcenter();
                    Coordinate c2 = t2.getCircumcenter();

                    edges.add(new VoronoiEdge(c1, c2));
                }
            }
        }

        return edges;
    }

    public List<VoronoiZone> generateZones(
            List<Hospital> hospitals,
            List<Patient> patients,
            List<DelaunayTriangle> triangles) {
        List<VoronoiZone> zones = new ArrayList<>();

        for (Hospital hospital : hospitals) {
            List<Coordinate> vertices = new ArrayList<>();

            for (DelaunayTriangle triangle : triangles) {
                if (triangle.containsHospital(hospital)) {
                    vertices.add(triangle.getCircumcenter());
                }
            }

            if (vertices.size() >= 3) {
                VoronoiZone zone = new VoronoiZone(
                        "Zone-" + hospital.getId(),
                        hospital,
                        vertices);

                for (Patient patient : patients) {
                    if (zone.contains(patient.getPosition())) {
                        zone.addPatient(patient);
                    }
                }

                zones.add(zone);
            }
        }

        return zones;
    }
}