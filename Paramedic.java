public class Paramedic {
    private String id;
    private String name;
    private Coordinate currentPosition;

    public Paramedic(String id, String name, Coordinate currentPosition) {
        this.id = id;
        this.name = name;
        this.currentPosition = currentPosition;
    }

    public void followItinerary(Itinerary itinerary) {
        for (Coordinate point : itinerary.getWaypoints()) {
            this.currentPosition = point;
            System.out.println(name + " moving to (" + point.getLatitude() + ", " + point.getLongitude() + ")");
        }
    }

    public void arriveAtHospital(Hospital hospital) {
        this.currentPosition = hospital.getPosition();
        System.out.println(name + " arrived at hospital " + hospital.getName());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Coordinate getCurrentPosition() { return currentPosition; }
}
