import java.util.ArrayList;
import java.util.List;

/**
 * Computes the Delaunay triangulation using the Bowyer-Watson algorithm.
 * The algorithm inserts points one by one and maintains the Delaunay property:
 * no point is inside the circumcircle of any triangle.
 */
public class Delaunay {

    private List<DelaunayTriangle> triangles;

    /** Creates a new Delaunay triangulation. */
    public Delaunay() {
        this.triangles = new ArrayList<>();
    }

    /** Builds the Delaunay triangulation from a list of hospitals. */
    public List<DelaunayTriangle> triangulate(List<Hospital> hospitals) {
        triangles.clear();

        // Create a super-triangle large enough to contain all hospitals
        DelaunayTriangle superTriangle = createSuperTriangle(hospitals);
        triangles.add(superTriangle);

        // Insert each hospital one by one into the triangulation
        for (Hospital hospital : hospitals) {
            insertPoint(hospital.getPosition());
        }

        // Remove all triangles connected to the super-triangle
        // They are artificial and not part of the real triangulation
        Coordinate[] superVertices = superTriangle.getVertices();
        triangles.removeIf(t -> sharesVertexWithSuper(t, superVertices));

        return triangles;
    }

    /** Inserts a single point into the current triangulation */
    private void insertPoint(Coordinate p) {

        // Find all triangles whose circumcircle contains p
        // These triangles violate the Delaunay property and must be removed
        List<DelaunayTriangle> badTriangles = new ArrayList<>();
        for (DelaunayTriangle t : triangles) {
            if (t.isInCircumcircle(p)) {
                badTriangles.add(t);
            }
        }

        // Find the boundary edges of the hole left by removing bad triangles
        // An edge is on the boundary if it belongs to only ONE bad triangle
        // (edges shared by two bad triangles are interior and disappear)
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
                    if (other == t) continue;
                    if (hasEdge(other, edge)) {
                        shared = true;
                        break;
                    }
                }
                // Keep only boundary edges (not shared)
                if (!shared) boundary.add(edge);
            }
        }

        // Remove bad triangles from the triangulation
        triangles.removeAll(badTriangles);

        // Re-triangulate the hole by connecting each boundary edge to p
        // p becomes a vertex of each new triangle
        for (Coordinate[] edge : boundary) {
            triangles.add(new DelaunayTriangle(edge[0], edge[1], p));
        }
    }

    /**
     * Returns true if the triangle contains the given edge.
     * Checks both directions (a->b and b->a) since edges are undirected.
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
     * Creates a super-triangle large enough to contain all hospitals.
     * Uses the bounding box of all hospitals, scaled by a factor of 10.
     */
    private DelaunayTriangle createSuperTriangle(List<Hospital> hospitals) {
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        // Find the bounding box of all hospitals
        for (Hospital h : hospitals) {
            double lat = h.getPosition().getLatitude();
            double lon = h.getPosition().getLongitude();
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }

        // Scale the bounding box to ensure all points are inside the super-triangle
        double delta = Math.max(maxLat - minLat, maxLon - minLon) * 10;

        Coordinate p1 = new Coordinate(minLat - delta, minLon - delta);
        Coordinate p2 = new Coordinate(minLat + (maxLat - minLat) / 2, maxLon + delta);
        Coordinate p3 = new Coordinate(maxLat + delta, minLon - delta);

        return new DelaunayTriangle(p1, p2, p3);
    }

    /** Returns true if the triangle shares a vertex with the super-triangle. */
    private boolean sharesVertexWithSuper(DelaunayTriangle t, Coordinate[] superVertices) {
        for (Coordinate v : t.getVertices()) {
            for (Coordinate sv : superVertices) {
                if (v.equals(sv)) return true;
            }
        }
        return false;
    }


    
    public List<DelaunayTriangle> getTriangles() {
        return triangles;
    }
}
