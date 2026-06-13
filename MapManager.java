import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Central facade coordinating all domain objects of the Emergency Dispatcher.
 *
 * <p>Stores hospitals and patients and ensures that the Delaunay triangulation,
 * the Voronoi diagram and the patient-to-hospital assignments are always kept
 * up to date after every modification.</p>

 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 * @see MapPersistenceService
 */
public class MapManager implements Serializable {

    /** Hospitals indexed by their unique identifier for O(1) look-up. */
    private Map<String, Hospital> hospitals;
    /** Patients indexed by their unique identifier for O(1) look-up. */
    private Map<String, Patient> patients;
    /** Delaunay engine; transient because it holds no independent state. */
    private transient Delaunay delaunay;

    /** Voronoi builder; transient because it is stateless. */
    private transient VoronoiService voronoiService;

    /** Most recent triangulation; rebuilt by {@link #recompute()}. */
    private transient List<DelaunayTriangle> triangles;

    /** Most recent Voronoi zones; rebuilt by {@link #recompute()}. */
    private transient List<VoronoiZone> zones;

    /** Most recent Voronoi edges; rebuilt by {@link #recompute()}. */
    private transient List<VoronoiEdge> voronoiEdges;

    /**
     * Creates an empty manager with all collections and services initialised.
     */
    public MapManager() {
        hospitals = new HashMap<>();
        patients = new HashMap<>();

        delaunay = new Delaunay();
        voronoiService = new VoronoiService();

        triangles = new ArrayList<>();
        zones = new ArrayList<>();
        voronoiEdges = new ArrayList<>();
    }

    /**
     * Adds a hospital and triggers a full geometry and assignment recomputation.
     *
     * @param hospital hospital to add; its id must be unique in this manager
     */
    public void addHospital(Hospital hospital) {
        hospitals.put(hospital.getId(), hospital);
        recompute();
    }

    /**
     * Adds a patient and triggers a full geometry and assignment recomputation.
     *
     * @param patient patient to add; its id must be unique in this manager
     */
    public void addPatient(Patient patient) {
        patients.put(patient.getId(), patient);
        recompute();
    }

    /**
     * Returns the hospital map indexed by identifier.
     *
     * @return live map of hospitals
     */
    public Map<String, Hospital> getHospitals() {
        return hospitals;
    }

    /**
     * Returns the patient map indexed by identifier.
     *
     * @return live map of patients
     */
    public Map<String, Patient> getPatients() {
        return patients;
    }

    /**
     * Removes a hospital by identifier and triggers a full recomputation.
     *
     * @param hospitalId identifier of the hospital to remove
     */
    public void removeHospital(String hospitalId) {
        hospitals.remove(hospitalId);
        recompute();
    }

    /**
     * Moves a hospital to a new position and triggers a full recomputation.
     *
     * @param hospitalId  identifier of the hospital to move
     * @param newPosition new geographic position
     * @throws IllegalArgumentException if no hospital with {@code hospitalId} exists
     */
    public void moveHospital(String hospitalId, Coordinate newPosition) {
        Hospital hospital = hospitals.get(hospitalId);

        if (hospital == null) {
            throw new IllegalArgumentException("Hospital not found: " + hospitalId);
        }

        hospital.updatePosition(newPosition);
        recompute();
    }

    /**
     * Removes a patient by identifier and triggers a full recomputation.
     *
     * @param patientId identifier of the patient to remove
     */
    public void removePatient(String patientId) {
        patients.remove(patientId);
        recompute();
    }

    /**
     * Moves a patient to a new position and triggers a full recomputation.
     *
     * @param patientId   identifier of the patient to move
     * @param newPosition new geographic position
     * @throws IllegalArgumentException if no patient with {@code patientId} exists
     */
    public void movePatient(String patientId, Coordinate newPosition) {
        Patient patient = patients.get(patientId);

        if (patient == null) {
            throw new IllegalArgumentException("Patient not found: " + patientId);
        }

        patient.updatePosition(newPosition);
        recompute();
    }

    /**
     * Rebuilds all derived geometric structures and patient assignments from
     * the current hospital and patient sets.
     *
     * <p>Called automatically after every state change to guarantee consistency.</p>
     */
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

    /**
     * Returns the most recently computed list of Delaunay triangles.
     *
     * @return list of triangles; empty if fewer than three hospitals are present
     */
    public List<DelaunayTriangle> getTriangles() {
        return triangles;
    }

    /**
     * Returns the most recently computed list of Voronoi zones.
     *
     * @return list of zones
     */
    public List<VoronoiZone> getZones() {
        return zones;
    }

    /**
     * Reinitialises transient fields after deserialisation and rebuilds the geometry.
     *
     * <p>Must be called immediately after loading a {@code MapManager} from a file
     * because Java serialisation skips {@code transient} fields.</p>
     */
    public void initializeAfterLoading() {
        delaunay = new Delaunay();
        voronoiService = new VoronoiService();
        voronoiEdges = new ArrayList<>();
        triangles = new ArrayList<>();
        zones = new ArrayList<>();

        recompute();
    }

    /**
     * Resets all hospital occupancy counters to zero and reassigns every patient
     * to the best available hospital.
     */
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

    /**
     * Returns the hospital currently assigned to the given patient.
     *
     * @param patient patient whose assignment is requested
     * @return the best matching hospital, or {@code null} if none is available
     */
    public Hospital getAssignedHospital(Patient patient) {

        return AssignmentService.findBestHospital(
                patient,
                new ArrayList<>(hospitals.values()));
    }

    /**
     * Returns the most recently computed list of Voronoi edges.
     *
     * @return list of Voronoi edges
     */
    public List<VoronoiEdge> getVoronoiEdges() {
        return voronoiEdges;
    }

    /**
     * Returns {@code true} if a hospital with the given identifier is registered.
     *
     * @param hospitalId identifier to look up
     * @return {@code true} if the hospital exists
     */
    public boolean hasHospital(String hospitalId) {
        return hospitals.containsKey(hospitalId);
    }

    /**
     * Returns {@code true} if a patient with the given identifier is registered.
     *
     * @param patientId identifier to look up
     * @return {@code true} if the patient exists
     */
    public boolean hasPatient(String patientId) {
        return patients.containsKey(patientId);
    }

    /**
     * Finds a hospital by geographic position.
     *
     * <p>Used to map a canvas click or a triangle vertex back to a hospital object.</p>
     *
     * @param position position to search for
     * @return the hospital at that position, or {@code null} if none matches
     */
    public Hospital findHospitalByPosition(Coordinate position) {

        for (Hospital hospital : hospitals.values()) {

            if (hospital.getPosition().equals(position)) {
                return hospital;
            }
        }

        return null;
    }

    /**
     * Removes all patients from the map and triggers a full recomputation.
     */
    public void clearPatients() {
    patients.clear();
    recompute();
    }

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

    public boolean hasHospital(String hospitalId) {
        return hospitals.containsKey(hospitalId);
    }

    public boolean hasPatient(String patientId) {
        return patients.containsKey(patientId);
    }

    public Hospital findHospitalByPosition(Coordinate position) {

        for (Hospital hospital : hospitals.values()) {

            if (hospital.getPosition().equals(position)) {
                return hospital;
            }
        }

        return null;
    }

    public void clearPatients() {
    patients.clear();
    recompute();
    }

}
