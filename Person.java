
import java.io.Serializable;
import java.util.Objects;

public abstract class Person implements Serializable{
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

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Person)) return false;

        Person other = (Person) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}