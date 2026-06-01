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
}