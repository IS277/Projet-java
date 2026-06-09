import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class MapManager {

    private Map<String, Hospital> hospitals;
    private Map<String, Patient> patients;
    private Delaunay delaunay;

    private VoronoiService voronoiService;

    private List<DelaunayTriangle> triangles;

    private List<VoronoiZone> zones;

    public MapManager() {
        hospitals = new HashMap<>();
        patients = new HashMap<>();

        delaunay = new Delaunay();
        voronoiService = new VoronoiService();

        triangles = new ArrayList<>();
        zones = new ArrayList<>();
    }

    public void addHospital(Hospital hospital) {
        hospitals.put(hospital.getId(), hospital);
        recompute();
    }

    public void addPatient(Patient patient) {
        patients.put(patient.getId(), patient);
        recompute();
    }

    public Map<String, Hospital> getHospitals() {
        return hospitals;
    }

    public Map<String, Patient> getPatients() {
        return patients;
    }

    public void removeHospital(String hospitalId) {
        hospitals.remove(hospitalId);
        recompute();
    }

    public void moveHospital(String hospitalId, Coordinate newPosition) {
        Hospital hospital = hospitals.get(hospitalId);

        if (hospital == null) {
            throw new IllegalArgumentException("Hospital not found: " + hospitalId);
        }

        hospital.updatePosition(newPosition);
        recompute();
    }

    public void removePatient(String patientId) {
        patients.remove(patientId);
        recompute();
    }

    public void movePatient(String patientId, Coordinate newPosition) {
        Patient patient = patients.get(patientId);

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        patient.updatePosition(newPosition);
        recompute();
    }

    public void recompute() {

        List<Hospital> hospitalList = new ArrayList<>(hospitals.values());

        triangles = delaunay.triangulate(
                hospitalList);

        zones = voronoiService.generateZones(
                hospitalList,
                new ArrayList<>(patients.values()),
                triangles);
    }

    public List<DelaunayTriangle> getTriangles() {
        return triangles;
    }

    public List<VoronoiZone> getZones() {
        return zones;
    }

}