import java.util.List;


public class Itinerary {

    private List<Coordinate> waypoints;
    private double totalDistance;

    public Itinerary(List<Coordinate> waypoints) {
        this.waypoints = waypoints;
        this.totalDistance = compute();
    }


    public double compute() {
        double total = 0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            total += waypoints.get(i).distanceTo(waypoints.get(i + 1));
        }
        return total;
    }

    
    public void displayPath() {
        for (Coordinate c : waypoints) {
            System.out.println("-> " + c);
        }
        System.out.println("Total distance: " + totalDistance);
    }

    

    public List<Coordinate> getWaypoints(){
        return waypoints;
    }
    public double getTotalDistance(){
        return totalDistance;
    }
}
