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
}