public abstract class Person {

    protected String id;
    protected String name;
    protected Coordinate position;

    public Person(String id, String name, Coordinate position) {
        this.id = id;
        this.name = name;
        this.position = position;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void updatePosition(Coordinate newPosition) {
        this.position = newPosition;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}