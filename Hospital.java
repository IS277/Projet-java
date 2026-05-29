public class Hospital {
    private String id;
    private String name;
    private Coordinate position;
    private int maxCapacity;
    private int currentCapacity;

    public Hospital(String id, String name, Coordinate position, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = 0;
    }

    public boolean isSaturated() {
        return currentCapacity >= maxCapacity;
    }

    public void updateCapacity(int n) {
        this.currentCapacity = n;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Coordinate getPosition() { return position; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentCapacity() { return currentCapacity; }
}
