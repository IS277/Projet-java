import java.util.Objects;

public class EmergencyUnit extends Person {

    private EmergencyUnitType type;

    private Hospital attachedHospital;

    public EmergencyUnit(
            String id,
            String name,
            Coordinate position,
            EmergencyUnitType type,
            Hospital attachedHospital
    ) {

        super(id, name, position);

        this.type = type;
        this.attachedHospital = attachedHospital;
    }

    public EmergencyUnitType getType() {
        return type;
    }

    public Hospital getAttachedHospital() {
        return attachedHospital;
    }

    public void setAttachedHospital(Hospital attachedHospital) {
        this.attachedHospital = attachedHospital;
    }

    @Override
    public String toString() {
        return "EmergencyUnit{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", type=" + type +
                ", attachedHospital=" +
                attachedHospital.getName() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EmergencyUnit)) {
            return false;
        }

        EmergencyUnit other = (EmergencyUnit) obj;

        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}