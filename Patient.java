public class Patient extends Person {

    private HospitalServiceType requiredService;

    public Patient(
            String id,
            String name,
            Coordinate position,
            HospitalServiceType requiredService
    ) {
        super(id, name, position);
        this.requiredService = requiredService;
    }

    public HospitalServiceType getRequiredService() {
        return requiredService;
    }

    public void setRequiredService(HospitalServiceType requiredService) {
        this.requiredService = requiredService;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", requiredService=" + requiredService +
                '}';
    }
}