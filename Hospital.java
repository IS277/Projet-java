import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.io.Serializable;

public class Hospital implements Serializable{

    private String id;

    private String name;

    private Coordinate position;

    private int maxCapacity;

    private int currentCapacity;

    private Set<HospitalServiceType> services;

    public Hospital(
            String id,
            String name,
            Coordinate position,
            int maxCapacity) {

        this.id = id;
        this.name = name;
        this.position = position;

        this.maxCapacity = maxCapacity;

        this.currentCapacity = 0;

        this.services = new HashSet<>();
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

    // Question : ajoute un service médical disponible
    public void addService(HospitalServiceType service) {
        services.add(service);
    }

    // Question : vérifie si un service existe
    public boolean hasService(HospitalServiceType service) {
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

    public Set<HospitalServiceType> getServices() {
        return new HashSet<>(services);
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

    public void updatePosition(Coordinate newPosition) {
        this.position = newPosition;
    }

    public boolean admitPatient() {
        if (isSaturated()) {
            return false;
        }

        currentCapacity++;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
