public class Coordinate {
    private double latitude;
    private double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(Coordinate c) {
        double dx = this.latitude - c.latitude;
        double dy = this.longitude - c.longitude;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
