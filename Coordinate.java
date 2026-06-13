import java.io.Serializable;
import java.util.Objects;

/**
 * Business class representing a geographic coordinate.
 *
 * A coordinate is defined by a latitude and a longitude.
 * It is used throughout the application to represent the
 * position of hospitals, patients and geometric structures
 * such as Delaunay triangles and Voronoi edges.
 *
 * The class implements {@link Serializable} so that map data
 * can be saved and restored using the persistence layer.
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class Coordinate implements Serializable {

    /**
     * Latitude of the coordinate.
     */
    private double latitude;

    /**
     * Longitude of the coordinate.
     */
    private double longitude;

    /**
     * Serialization identifier used when saving objects.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new coordinate.
     *
     * @param latitude latitude value
     * @param longitude longitude value
     */
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Computes the Euclidean distance between this coordinate
     * and another coordinate.
     *
     * For the purposes of this project, coordinates are handled
     * in a simplified Cartesian space. This approximation is
     * sufficient because the goal is to compare relative
     * distances between hospitals and patients.
     *
     * @param c destination coordinate
     * @return Euclidean distance between the two coordinates
     */
    public double distanceTo(Coordinate c) {

        // A simple distance model is sufficient for the simulation.
        double dx = this.latitude - c.latitude;
        double dy = this.longitude - c.longitude;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the latitude of the coordinate.
     *
     * @return latitude value
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude of the coordinate.
     *
     * @return longitude value
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns a textual representation of the coordinate.
     *
     * Mainly used for debugging, logging and console output.
     *
     * @return formatted coordinate description
     */
    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    /**
     * Compares this coordinate with another object.
     *
     * Two coordinates are considered equal when they have
     * exactly the same latitude and longitude values.
     *
     * @param obj object to compare with
     * @return true if both coordinates are identical
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Coordinate)) {
            return false;
        }

        Coordinate other = (Coordinate) obj;

        return Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0;
    }

    /**
     * Generates a hash code consistent with the equals method.
     *
     * This allows Coordinate objects to be safely used in
     * hash-based collections such as HashSet and HashMap.
     *
     * @return hash code of the coordinate
     */
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
