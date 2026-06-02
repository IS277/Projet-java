public class Patient extends Person {

    public Patient(String id, String name, Coordinate position) {
        super(id, name, position);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}