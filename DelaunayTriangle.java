import java.util.ArrayList;
import java.util.List;

/**
 * Represents a triangle in the Delaunay triangulation.
 */
public class DelaunayTriangle {

    private Coordinate[] vertices;

    /** Creates a triangle from three coordinates. */
    public DelaunayTriangle(Coordinate a, Coordinate b, Coordinate c) {
        this.vertices = new Coordinate[] { a, b, c };
    }

    /** Returns true if the given coordinate is inside the circumcircle. */
    public boolean isInCircumcircle(Coordinate p) {

        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();
        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();
        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        // The circumcenter (ux, uy) is the point equidistant from all 3 vertices
        // Found by solving the system: distance(O,A) = distance(O,B) = distance(O,C)
        double d = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));

        double ux = ((ax * ax + ay * ay) * (by - cy)
                + (bx * bx + by * by) * (cy - ay)
                + (cx * cx + cy * cy) * (ay - by)) / d;

        double uy = ((ax * ax + ay * ay) * (cx - bx)
                + (bx * bx + by * by) * (ax - cx)
                + (cx * cx + cy * cy) * (bx - ax)) / d;

        // p is inside if its distance to the circumcenter is less than the circumradius
        double radius = Math.sqrt((ax - ux) * (ax - ux) + (ay - uy) * (ay - uy));

        double dist = Math.sqrt((p.getLatitude() - ux) * (p.getLatitude() - ux)
                + (p.getLongitude() - uy) * (p.getLongitude() - uy));

        return dist < radius;
    }

    public void display() {
        System.out.println("Triangle: " + vertices[0] + " " + vertices[1] + " " + vertices[2]);
    }

    public Coordinate[] getVertices() {
        return vertices;
    }

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

    public boolean containsHospital(Hospital hospital) {
        Coordinate position = hospital.getPosition();

        for (Coordinate vertex : vertices) {
            if (vertex.equals(position)) {
                return true;
            }
        }

        return false;
    }

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

    public double getDistanceAB() {
        return vertices[0].distanceTo(vertices[1]);
    }

    public double getDistanceBC() {
        return vertices[1].distanceTo(vertices[2]);
    }

    public double getDistanceCA() {
        return vertices[2].distanceTo(vertices[0]);
    }

    @Override
    public String toString() {
        return "Triangle[" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]";
    }
}
