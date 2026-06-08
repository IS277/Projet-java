import java.util.HashMap;
import java.util.Map;

public class MapManager {

    private Map<String, Hospital> hospitals;
    private Map<String, Patient> patients;

    public MapManager() {
        hospitals = new HashMap<>();
        patients = new HashMap<>();
    }

    public void addHospital(Hospital hospital) {
        hospitals.put(hospital.getId(), hospital);
    }

    public void addPatient(Patient patient) {
        patients.put(patient.getId(), patient);
    }

    public Map<String, Hospital> getHospitals() {
        return hospitals;
    }

    public Map<String, Patient> getPatients() {
        return patients;
    }

    public void removeHospital(String hospitalId) {
        hospitals.remove(hospitalId);
    }

    public void moveHospital(String hospitalId, Coordinate newPosition) {
        Hospital hospital = hospitals.get(hospitalId);

        if (hospital == null) {
            throw new IllegalArgumentException("Hospital not found: " + hospitalId);
        }

        hospital.updatePosition(newPosition);
    }

    public void removePatient(String patientId) {
        patients.remove(patientId);
    }

    public void movePatient(String patientId, Coordinate newPosition) {
        Patient patient = patients.get(patientId);

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        patient.updatePosition(newPosition);
    }

}