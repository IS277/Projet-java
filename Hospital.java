

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Hospital {

    private String id;

    private String name;

    private Coordinate position;

    private int maxCapacity;

    private int currentCapacity;

    private List<String> services;

    public Hospital(
            String id,
            String name,
            Coordinate position,
            int maxCapacity
    ) {

        this.id = id;
        this.name = name;
        this.position = position;

        this.maxCapacity = maxCapacity;

        this.currentCapacity = 0;

        this.services = new ArrayList<>();
    }


    public double getSaturationRate() {
        return (double) currentCapacity / maxCapacity;
    }
    



    public boolean isSaturated() {
        return currentCapacity >= maxCapacity;
    }

    // Question : met à jour le nombre de patients présents
    public void updateCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }


    public void addService(String service) {
        services.add(service);
    }

    // Question : vérifie si un service existe
    public boolean hasService(String service) {
        return services.contains(service);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinate getPosition() {
        return position;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public List<String> getServices() {
        return services;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", maxCapacity=" + maxCapacity +
                ", currentCapacity=" + currentCapacity +
                ", services=" + services +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Hospital)) {
            return false;
        }

        Hospital other = (Hospital) obj;

        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

