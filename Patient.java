public class Patient {
    private String id;
    private String name;
    private Coordinate position;

    public Patient(String id, String name, Coordinate position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public Coordinate getPosition() { return position; }
    public void updatePosition(Coordinate newPosition) {
        this.position = newPosition;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
