import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class MapManager implements Serializable {

    private Map<String, Hospital> hospitals;
    private Map<String, Patient> patients;
    private transient Delaunay delaunay;

    private transient VoronoiService voronoiService;

    private transient List<DelaunayTriangle> triangles;

    private transient List<VoronoiZone> zones;

    private transient List<VoronoiEdge> voronoiEdges;

    public MapManager() {
        hospitals = new HashMap<>();
        patients = new HashMap<>();

        delaunay = new Delaunay();
        voronoiService = new VoronoiService();

        triangles = new ArrayList<>();
        zones = new ArrayList<>();
        voronoiEdges = new ArrayList<>();
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

        voronoiEdges = voronoiService.getVoronoiEdges(triangles);

        recomputeAssignments();
    }

    public List<DelaunayTriangle> getTriangles() {
        return triangles;
    }

    public List<VoronoiZone> getZones() {
        return zones;
    }

    public void initializeAfterLoading() {
        delaunay = new Delaunay();
        voronoiService = new VoronoiService();
        voronoiEdges = new ArrayList<>();
        triangles = new ArrayList<>();
        zones = new ArrayList<>();

        recompute();
    }

    public void recomputeAssignments() {

        for (Hospital hospital : hospitals.values()) {
            hospital.updateCapacity(0);
        }

        
        for (Patient patient : patients.values()) {
            Hospital hospital = AssignmentService.findBestHospital(
                    patient,
                    new ArrayList<>(hospitals.values()));

            if (hospital != null) {
                hospital.admitPatient();
            }
        }
    }

    public Hospital getAssignedHospital(Patient patient) {

        return AssignmentService.findBestHospital(
                patient,
                new ArrayList<>(hospitals.values()));
    }

    public List<VoronoiEdge> getVoronoiEdges() {
        return voronoiEdges;
    }

}