import java.util.Objects;

/**
 * Geometry class representing an edge of the Voronoi diagram.
 *
 * A Voronoi edge is defined by two coordinates: a start point and an end
 * point. In this project, Voronoi edges are generated from neighboring
 * {@link DelaunayTriangle} circumcenters and represent the boundaries between
 * hospital influence zones.
 *
 * The complete set of Voronoi edges forms the Voronoi diagram associated with
 * the Delaunay triangulation.
 *
 * <p><b>Class type:</b> Geometry / Model class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class VoronoiEdge {

    /**
     * Starting coordinate of the Voronoi edge.
     */
    private Coordinate start;

    /**
     * Ending coordinate of the Voronoi edge.
     */
    private Coordinate end;

    /**
     * Creates a new Voronoi edge.
     *
     * @param start starting coordinate of the edge
     * @param end ending coordinate of the edge
     */
    public VoronoiEdge(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the starting coordinate of the edge.
     *
     * @return start coordinate
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * Returns the ending coordinate of the edge.
     *
     * @return end coordinate
     */
    public Coordinate getEnd() {
        return end;
    }

    /**
     * Returns a textual representation of the Voronoi edge.
     *
     * This method is mainly useful for debugging, logging and console-based
     * displays during development and testing.
     *
     * @return complete edge description
     */
    @Override
    public String toString() {
        return "VoronoiEdge{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    /**
     * Compares this Voronoi edge with another object.
     *
     * Two Voronoi edges are considered equal when they have the same start
     * coordinate and the same end coordinate.
     *
     * @param obj object to compare
     * @return true if both edges are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof VoronoiEdge)) {
            return false;
        }

        VoronoiEdge other = (VoronoiEdge) obj;

        return Objects.equals(start, other.start)
                && Objects.equals(end, other.end);
    }

    /**
     * Generates a hash code consistent with equals().
     *
     * This method allows VoronoiEdge objects to be correctly stored and
     * retrieved from hash-based collections such as {@link java.util.HashSet}
     * and {@link java.util.HashMap}.
     *
     * @return edge hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}