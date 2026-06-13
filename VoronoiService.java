import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for constructing the Voronoi diagram from a Delaunay triangulation.
 *
 * <p>Exploits the dual relationship between Delaunay triangulations and Voronoi diagrams:
 * each triangle contributes one Voronoi vertex (its circumcenter), and each pair of
 * adjacent triangles contributes one Voronoi edge connecting their circumcenters.</p>
 *
 * <p>This class is stateless; all methods operate exclusively on their parameters.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 * @see Delaunay
 * @see VoronoiZone
 */
public class VoronoiService {

    /**
     * Extracts one Voronoi vertex per Delaunay triangle by computing each circumcenter.
     *
     * @param triangles list of Delaunay triangles produced by {@link Delaunay#triangulate}
     * @return list of Voronoi vertices in the same order as the input triangles
     */
    public List<Coordinate> getVoronoiVertices(List<DelaunayTriangle> triangles) {
        List<Coordinate> vertices = new ArrayList<>();

        for (DelaunayTriangle triangle : triangles) {
            vertices.add(triangle.getCircumcenter());
        }

        return vertices;
    }

    /**
     * Produces one Voronoi edge for each pair of adjacent Delaunay triangles.
     *
     * <p>Two triangles are adjacent when they share exactly two vertices.
     * Only the upper half of the adjacency matrix is visited to avoid duplicates.</p>
     *
     * @param triangles list of Delaunay triangles produced by {@link Delaunay#triangulate}
     * @return list of Voronoi edges; empty if fewer than two triangles are present
     */
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

    /**
     * Generates one {@link VoronoiZone} per hospital and populates each zone
     * with the patients whose positions fall inside it.
     *
     * <p>The polygon of each zone is formed by collecting the circumcenters of all
     * triangles that contain the hospital as a vertex. At least three circumcenters
     * are required; hospitals with fewer are skipped.</p>
     *
     * @param hospitals list of hospitals acting as Voronoi sites
     * @param patients  list of patients to distribute across the generated zones
     * @param triangles list of Delaunay triangles computed from the hospitals
     * @return list of {@link VoronoiZone} instances; one per hospital with a valid polygon
     */
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
