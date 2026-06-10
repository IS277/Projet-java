import java.io.Serializable;
import java.util.Objects;

public class Coordinate implements Serializable{
    private double latitude;
    private double longitude;
    private static final long serialVersionUID = 1L;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Approximation only — valid for short distances.
    public double distanceTo(Coordinate c) {
        double dx = this.latitude - c.latitude;
        double dy = this.longitude - c.longitude;
        return Math.sqrt(dx * dx + dy * dy);
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Coordinate)) return false;

        Coordinate other = (Coordinate) obj;
        return Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}

