import java.util.Objects;

/**
 * Geometry class representing a triangle in the Delaunay triangulation.
 *
 * A triangle is defined by three vertices represented by {@link Coordinate}
 * objects. This class provides the geometric operations required to build and
 * analyze the Delaunay triangulation, such as circumcircle testing,
 * circumcenter computation, surface calculation and edge comparison.
 *
 * The circumcenter of a Delaunay triangle is also used as a vertex of the
 * corresponding Voronoi diagram.
 *
 * <p><b>Class type:</b> Geometry / Model class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class DelaunayTriangle {

    /**
     * Vertices of the triangle.
     */
    private Coordinate[] vertices;

    /**
     * Creates a triangle from three coordinates.
     *
     * @param a first vertex of the triangle
     * @param b second vertex of the triangle
     * @param c third vertex of the triangle
     */
    public DelaunayTriangle(Coordinate a, Coordinate b, Coordinate c) {
        this.vertices = new Coordinate[] { a, b, c };
    }

    /**
     * Checks whether a coordinate lies inside the circumcircle of the triangle.
     *
     * This method is essential in the Bowyer-Watson algorithm. When a new point
     * is inserted, every triangle whose circumcircle contains that point no
     * longer satisfies the Delaunay property and must be removed.
     *
     * @param p coordinate to test
     * @return true if the coordinate is inside the circumcircle, false otherwise
     */
    public boolean isInCircumcircle(Coordinate p) {

        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();
        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();
        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        // The circumcenter is equidistant from the three vertices.
        double d = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));

        double ux = ((ax * ax + ay * ay) * (by - cy)
                + (bx * bx + by * by) * (cy - ay)
                + (cx * cx + cy * cy) * (ay - by)) / d;

        double uy = ((ax * ax + ay * ay) * (cx - bx)
                + (bx * bx + by * by) * (ax - cx)
                + (cx * cx + cy * cy) * (bx - ax)) / d;

        double radius = Math.sqrt((ax - ux) * (ax - ux) + (ay - uy) * (ay - uy));

        double dist = Math.sqrt((p.getLatitude() - ux) * (p.getLatitude() - ux)
                + (p.getLongitude() - uy) * (p.getLongitude() - uy));

        return dist < radius;
    }

    /**
     * Returns the three vertices of the triangle.
     *
     * @return array containing the triangle vertices
     */
    public Coordinate[] getVertices() {
        return vertices;
    }

    /**
     * Computes the circumcenter of the triangle.
     *
     * The circumcenter is the center of the circle passing through the three
     * vertices. In this project, this point is used to build Voronoi diagram
     * vertices from the Delaunay triangulation.
     *
     * @return circumcenter of the triangle
     */
    public Coordinate getCircumcenter() {

        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();

        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();

        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        double d = 2 * (ax * (by - cy)
                + bx * (cy - ay)
                + cx * (ay - by));

        double ux = ((ax * ax + ay * ay) * (by - cy)
                + (bx * bx + by * by) * (cy - ay)
                + (cx * cx + cy * cy) * (ay - by))
                / d;

        double uy = ((ax * ax + ay * ay) * (cx - bx)
                + (bx * bx + by * by) * (ax - cx)
                + (cx * cx + cy * cy) * (bx - ax))
                / d;

        return new Coordinate(ux, uy);
    }

    /**
     * Checks whether this triangle shares an edge with another triangle.
     *
     * Two triangles share an edge when they have exactly two vertices in
     * common. This relationship is used when creating Voronoi edges between
     * neighboring Delaunay triangles.
     *
     * @param other triangle to compare with
     * @return true if both triangles share an edge, false otherwise
     */
    public boolean sharesEdgeWith(DelaunayTriangle other) {
        int commonVertices = 0;

        for (Coordinate vertex : this.vertices) {
            for (Coordinate otherVertex : other.getVertices()) {
                if (vertex.equals(otherVertex)) {
                    commonVertices++;
                }
            }
        }

        return commonVertices == 2;
    }

    /**
     * Checks whether the triangle contains the position of a hospital.
     *
     * @param hospital hospital to test
     * @return true if the hospital position is one of the triangle vertices
     */
    public boolean containsHospital(Hospital hospital) {
        Coordinate position = hospital.getPosition();

        for (Coordinate vertex : vertices) {
            if (vertex.equals(position)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Computes the surface area of the triangle.
     *
     * @return triangle surface area
     */
    public double getSurface() {
        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();

        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();

        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        return Math.abs(
                ax * (by - cy)
                        + bx * (cy - ay)
                        + cx * (ay - by))
                / 2.0;
    }

    /**
     * Computes the length of edge AB.
     *
     * @return distance between the first and second vertices
     */
    public double getDistanceAB() {
        return vertices[0].distanceTo(vertices[1]);
    }

    /**
     * Computes the length of edge BC.
     *
     * @return distance between the second and third vertices
     */
    public double getDistanceBC() {
        return vertices[1].distanceTo(vertices[2]);
    }

    /**
     * Computes the length of edge CA.
     *
     * @return distance between the third and first vertices
     */
    public double getDistanceCA() {
        return vertices[2].distanceTo(vertices[0]);
    }

    /**
     * Returns a textual representation of the triangle.
     *
     * @return triangle description
     */
    @Override
    public String toString() {
        return "Triangle[" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]";
    }

    /**
     * Compares this triangle with another object.
     *
     * Two triangles are considered equal when they have the same vertices
     * in the same order.
     *
     * @param obj object to compare
     * @return true if both triangles are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DelaunayTriangle)) {
            return false;
        }

        DelaunayTriangle other = (DelaunayTriangle) obj;

        return vertices[0].equals(other.vertices[0])
                && vertices[1].equals(other.vertices[1])
                && vertices[2].equals(other.vertices[2]);
    }

    /**
     * Generates a hash code consistent with equals().
     *
     * @return triangle hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(vertices[0], vertices[1], vertices[2]);
    }
}