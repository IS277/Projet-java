public class Paramedic extends Person {

    public Paramedic(String id, String name, Coordinate currentPosition) {
        super(id, name, currentPosition);
    }

    public void followItinerary(Itinerary itinerary) {
        for (Coordinate point : itinerary.getWaypoints()) {
            this.position = point;
            System.out.println(name + " moving to " + point);
        }
    }

    public void arriveAtHospital(Hospital hospital) {
        this.position = hospital.getPosition();
        System.out.println(name + " arrived at " + hospital.getName());
    }
}
