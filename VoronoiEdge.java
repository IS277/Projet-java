import java.util.Objects;

public class VoronoiEdge {

    private Coordinate start;
    private Coordinate end;

    public VoronoiEdge(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "VoronoiEdge{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}