import java.util.ArrayList;
import java.util.List;

/**
 * Algorithm class responsible for computing the Delaunay triangulation.
 *
 * This class implements the Bowyer-Watson algorithm. Points are inserted
 * one by one, and the triangulation is updated after each insertion in order
 * to preserve the Delaunay property: no inserted point should lie inside the
 * circumcircle of any triangle in the final triangulation.
 *
 * In this project, hospital positions are used as input points. The resulting
 * triangles are then used to build the Voronoi diagram.
 *
 * <p><b>Class type:</b> Algorithm / Geometry service class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class Delaunay {

    /**
     * Current list of triangles in the triangulation.
     */
    private List<DelaunayTriangle> triangles;

    /**
     * Creates an empty Delaunay triangulation.
     */
    public Delaunay() {
        this.triangles = new ArrayList<>();
    }

    /**
     * Builds the Delaunay triangulation from a list of hospitals.
     *
     * The method first creates a large artificial triangle containing all
     * hospital positions. Then, hospitals are inserted one by one. At the end,
     * triangles connected to the artificial super-triangle are removed because
     * they do not belong to the real triangulation.
     *
     * @param hospitals hospitals used as input points
     * @return list of Delaunay triangles generated from hospital positions
     */
    public List<DelaunayTriangle> triangulate(List<Hospital> hospitals) {
        triangles.clear();

        DelaunayTriangle superTriangle = createSuperTriangle(hospitals);
        triangles.add(superTriangle);

        // Hospitals are inserted progressively to maintain the Delaunay property.
        for (Hospital hospital : hospitals) {
            insertPoint(hospital.getPosition());
        }

        // Artificial triangles must be removed from the final triangulation.
        Coordinate[] superVertices = superTriangle.getVertices();
        triangles.removeIf(t -> sharesVertexWithSuper(t, superVertices));

        return triangles;
    }

    /**
     * Inserts one point into the current triangulation.
     *
     * Triangles whose circumcircle contains the inserted point are invalidated.
     * They are removed, and the resulting hole is filled by creating new
     * triangles connected to the inserted point.
     *
     * @param p point to insert into the triangulation
     */
    private void insertPoint(Coordinate p) {

        List<DelaunayTriangle> badTriangles = new ArrayList<>();

        // These triangles violate the Delaunay property for the inserted point.
        for (DelaunayTriangle t : triangles) {
            if (t.isInCircumcircle(p)) {
                badTriangles.add(t);
            }
        }

        List<Coordinate[]> boundary = new ArrayList<>();

        for (DelaunayTriangle t : badTriangles) {
            Coordinate[] v = t.getVertices();

            Coordinate[][] edges = {
                {v[0], v[1]},
                {v[1], v[2]},
                {v[2], v[0]}
            };

            for (Coordinate[] edge : edges) {
                boolean shared = false;

                for (DelaunayTriangle other : badTriangles) {
                    if (other == t) {
                        continue;
                    }

                    if (hasEdge(other, edge)) {
                        shared = true;
                        break;
                    }
                }

                // Shared edges are inside the hole and must disappear.
                if (!shared) {
                    boundary.add(edge);
                }
            }
        }

        triangles.removeAll(badTriangles);

        // The hole is retriangulated by linking each boundary edge to the new point.
        for (Coordinate[] edge : boundary) {
            triangles.add(new DelaunayTriangle(edge[0], edge[1], p));
        }
    }

    /**
     * Checks whether a triangle contains a given edge.
     *
     * Edges are treated as undirected, so both vertex orders are accepted.
     *
     * @param t triangle to inspect
     * @param edge edge represented by two coordinates
     * @return true if the triangle contains the given edge
     */
    private boolean hasEdge(DelaunayTriangle t, Coordinate[] edge) {
        Coordinate[] v = t.getVertices();

        for (int i = 0; i < 3; i++) {
            Coordinate a = v[i];
            Coordinate b = v[(i + 1) % 3];

            if ((a.equals(edge[0]) && b.equals(edge[1]))
                    || (a.equals(edge[1]) && b.equals(edge[0]))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a super-triangle large enough to contain all hospital positions.
     *
     * The super-triangle is an artificial starting structure required by the
     * Bowyer-Watson algorithm. It is removed after all real points have been
     * inserted.
     *
     * @param hospitals hospitals that must be contained in the super-triangle
     * @return artificial triangle enclosing all hospital positions
     */
    private DelaunayTriangle createSuperTriangle(List<Hospital> hospitals) {
        double minLat = Double.MAX_VALUE;
        double maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = -Double.MAX_VALUE;

        for (Hospital h : hospitals) {
            double lat = h.getPosition().getLatitude();
            double lon = h.getPosition().getLongitude();

            if (lat < minLat) {
                minLat = lat;
            }

            if (lat > maxLat) {
                maxLat = lat;
            }

            if (lon < minLon) {
                minLon = lon;
            }

            if (lon > maxLon) {
                maxLon = lon;
            }
        }

        // The bounding box is enlarged to guarantee that all points are enclosed.
        double delta = Math.max(maxLat - minLat, maxLon - minLon) * 10;

        Coordinate p1 = new Coordinate(minLat - delta, minLon - delta);
        Coordinate p2 = new Coordinate(minLat + (maxLat - minLat) / 2, maxLon + delta);
        Coordinate p3 = new Coordinate(maxLat + delta, minLon - delta);

        return new DelaunayTriangle(p1, p2, p3);
    }

    /**
     * Checks whether a triangle shares a vertex with the super-triangle.
     *
     * Such triangles are removed because they depend on artificial points
     * used only during initialization.
     *
     * @param t triangle to check
     * @param superVertices vertices of the super-triangle
     * @return true if the triangle shares at least one vertex with the super-triangle
     */
    private boolean sharesVertexWithSuper(DelaunayTriangle t, Coordinate[] superVertices) {
        for (Coordinate v : t.getVertices()) {
            for (Coordinate sv : superVertices) {
                if (v.equals(sv)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the current triangles of the triangulation.
     *
     * @return current list of Delaunay triangles
     */
    public List<DelaunayTriangle> getTriangles() {
        return triangles;
    }
}