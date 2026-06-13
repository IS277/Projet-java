import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the geographic influence zone of a single hospital in the Voronoi diagram.
 *
 * <p>A {@code VoronoiZone} is a convex polygon whose vertices are the circumcenters
 * of the Delaunay triangles that contain the associated hospital.
 * It tracks the patients located inside it and exposes statistical indicators
 * (surface, perimeter, density, distance metrics, service breakdown) used by the
 * graphical views and the dispatcher for decision support.</p>
 *
 * <p>Implements {@link Comparable} so that zones can be ranked by surface area.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 * @see VoronoiService
 */
public class VoronoiZone implements Comparable<VoronoiZone> {

    /** Unique identifier used to distinguish zones in collections and persistence. */
    private final String id;
    /** Hospital whose influence region this zone represents. */
    private final Hospital hospital;
    /** Ordered list of polygon vertices defining the zone boundary. */
    private List<Coordinate> vertices;
    /**
     * Pre-computed surface area kept in sync with {@link #vertices}
     * to avoid recomputing it on every statistics query.
     */
    private double surface;
    /**
     * Patients located inside this zone, stored in a {@link java.util.HashSet}
     * to prevent duplicates and allow O(1) membership tests.
     */
    private Set<Patient> patients;

    /**
     * Constructs a Voronoi zone and immediately computes its surface area.
     *
     * @param id       non-blank identifier for this zone
     * @param hospital hospital associated with this zone; must not be {@code null}
     * @param vertices polygon vertices; at least three required for a valid polygon
     * @throws IllegalArgumentException if {@code id} is blank, {@code hospital} is
     *                                  {@code null}, or fewer than three vertices are supplied
     */
    public VoronoiZone(String id, Hospital hospital, List<Coordinate> vertices) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Zone id cannot be empty");
        if (hospital == null)
            throw new IllegalArgumentException("Hospital cannot be null");
        if (vertices == null || vertices.size() < 3)
            throw new IllegalArgumentException("A zone needs at least 3 vertices");

        this.id = id;
        this.hospital = hospital;
        this.vertices = new ArrayList<>(vertices);
        this.surface = computeSurface();
        this.patients = new HashSet<>();
    }

    /**
     * Tests whether a coordinate lies inside this zone using the ray-casting algorithm.
     *
     * <p>Vertices are sorted by polar angle around the hospital before the test
     * because the raw vertex list may be unordered.</p>
     *
     * @param c coordinate whose membership is to be tested
     * @return {@code true} if {@code c} is inside this zone
     */
    public boolean contains(Coordinate c) {
        // Sort from hospital position — it is guaranteed to be inside its own Voronoi cell
        Coordinate ref = hospital.getPosition();
        List<Coordinate> sorted = new ArrayList<>(vertices);
        sorted.sort(Comparator.comparingDouble(v ->
            Math.atan2(v.getLongitude() - ref.getLongitude(),
                       v.getLatitude()  - ref.getLatitude())));

        int n = sorted.size();
        boolean inside = false;
        double x = c.getLatitude();
        double y = c.getLongitude();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = sorted.get(i).getLatitude(), yi = sorted.get(i).getLongitude();
            double xj = sorted.get(j).getLatitude(), yj = sorted.get(j).getLongitude();

            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect)
                inside = !inside;
        }
        return inside;
    }

    /**
     * Computes the surface area using the shoelace formula and stores the result.
     *
     * @return the computed surface area in squared coordinate units
     */
    public double computeSurface() {
        int n = vertices.size();
        double area = 0;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = vertices.get(i).getLatitude(), yi = vertices.get(i).getLongitude();
            double xj = vertices.get(j).getLatitude(), yj = vertices.get(j).getLongitude();
            area += (xj + xi) * (yj - yi);
        }

        this.surface = Math.abs(area) / 2.0;
        return this.surface;
    }

    /**
     * Returns the centroid as the arithmetic mean of the polygon vertices.
     *
     * @return centroid coordinate
     */
    public Coordinate getCenter() {
        double sumLat = 0;
        double sumLon = 0;

        for (Coordinate v : vertices) {
            sumLat += v.getLatitude();
            sumLon += v.getLongitude();
        }

        return new Coordinate(sumLat / vertices.size(), sumLon / vertices.size());
    }

    /**
     * Returns the Euclidean distance from the given coordinate to the zone centroid.
     *
     * @param c the coordinate from which to measure
     * @return distance in coordinate units
     */
    public double distanceToCenter(Coordinate c) {
        return c.distanceTo(getCenter());
    }

    /**
     * Appends a vertex to the polygon and recomputes the surface area.
     *
     * @param vertex vertex to add; must not be {@code null}
     * @throws IllegalArgumentException if {@code vertex} is {@code null}
     */
    public void addVertex(Coordinate vertex) {
        if (vertex == null)
            throw new IllegalArgumentException("Vertex cannot be null");
        vertices.add(vertex);
        computeSurface();
    }

    /**
     * Removes a vertex from the polygon and recomputes the surface area.
     *
     * @param vertex vertex to remove
     * @throws IllegalStateException if removal would leave fewer than three vertices
     */
    public void removeVertex(Coordinate vertex) {
        if (vertices.size() <= 3)
            throw new IllegalStateException("Cannot remove vertex: zone must keep at least 3 vertices");
        vertices.remove(vertex);
        computeSurface();
    }

    /**
     * Registers a patient as belonging to this zone.
     *
     * @param patient patient to register; must not be {@code null}
     * @throws IllegalArgumentException if {@code patient} is {@code null}
     */
    public void addPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        patients.add(patient);
    }

    /**
     * Returns the number of patients currently registered in this zone.
     *
     * @return patient count; zero if no patients have been assigned
     */
    public int getPatientCount() {
        return patients.size();
    }

    /**
     * Returns the patient density (patients per surface unit).
     *
     * @return density, or {@code 0} if the surface is zero
     */
    public double getDensity() {
        if (surface == 0) {
            return 0;
        }

        return patients.size() / surface;
    }

    /**
     * Returns the average distance from patients in this zone to the hospital.
     *
     * @return mean distance, or {@code 0} if no patients are present
     */
    public double getAverageDistanceToHospital() {
        if (patients.isEmpty()) {
            return 0;
        }

        double totalDistance = 0;

        for (Patient patient : patients) {
            totalDistance += patient.getPosition()
                    .distanceTo(hospital.getPosition());
        }

        return totalDistance / patients.size();
    }

    /**
     * Returns a defensive copy of the patient set to prevent external mutation.
     *
     * @return new set containing the current patients
     */
    public Set<Patient> getPatients() {
        return new HashSet<>(patients);
    }

    @Override
    public int compareTo(VoronoiZone other) {
        return Double.compare(this.surface, other.surface);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof VoronoiZone))
            return false;
        VoronoiZone other = (VoronoiZone) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "VoronoiZone{" +
                "id='" + id + '\'' +
                ", hospital='" + hospital.getName() + '\'' +
                ", vertices=" + vertices.size() +
                ", surface=" + String.format("%.4f", surface) +
                '}';
    }

    public String getId() {
        return id;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public List<Coordinate> getVertices() {
        return new ArrayList<>(vertices);
    }

    public double getSurface() {
        return surface;
    }

    public double getMinDistanceToHospital() {
        if (patients.isEmpty()) {
            return 0;
        }

        double minDistance = Double.MAX_VALUE;

        for (Patient patient : patients) {
            double distance = patient.getPosition()
                    .distanceTo(hospital.getPosition());

            if (distance < minDistance) {
                minDistance = distance;
            }
        }

        return minDistance;
    }

    public double getMaxDistanceToHospital() {
        if (patients.isEmpty()) return 0;
        double maxDistance = 0;
        for (Patient patient : patients) {
            double d = patient.getPosition().distanceTo(hospital.getPosition());
            if (d > maxDistance) maxDistance = d;
        }
        return maxDistance;
    }

    public double getStdDevDistanceToHospital() {
        if (patients.size() < 2) return 0;
        double avg = getAverageDistanceToHospital();
        double sumSq = 0;
        for (Patient p : patients)
            sumSq += Math.pow(p.getPosition().distanceTo(hospital.getPosition()) - avg, 2);
        return Math.sqrt(sumSq / patients.size());
    }

    public double getPerimeter() {
        if (vertices.size() < 2) return 0;
        Coordinate ref = hospital.getPosition();
        List<Coordinate> sorted = new ArrayList<>(vertices);
        sorted.sort(Comparator.comparingDouble(v ->
            Math.atan2(v.getLongitude() - ref.getLongitude(),
                       v.getLatitude()  - ref.getLatitude())));
        double perim = 0;
        for (int i = 0; i < sorted.size(); i++) {
            Coordinate a = sorted.get(i);
            Coordinate b = sorted.get((i + 1) % sorted.size());
            perim += a.distanceTo(b);
        }
        return perim;
    }

    public Map<HospitalServiceType, Long> getPatientsByService() {
        Map<HospitalServiceType, Long> counts = new EnumMap<>(HospitalServiceType.class);
        for (Patient p : patients)
            counts.merge(p.getRequiredService(), 1L, Long::sum);
        return counts;
    }
}
