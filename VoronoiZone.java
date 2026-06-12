import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

public class VoronoiZone implements Comparable<VoronoiZone> {

    private final String id;
    private final Hospital hospital;
    private List<Coordinate> vertices;
    private double surface;
    private Set<Patient> patients;

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

    public Coordinate getCenter() {
        double sumLat = 0;
        double sumLon = 0;

        for (Coordinate v : vertices) {
            sumLat += v.getLatitude();
            sumLon += v.getLongitude();
        }

        return new Coordinate(sumLat / vertices.size(), sumLon / vertices.size());
    }

    public double distanceToCenter(Coordinate c) {
        return c.distanceTo(getCenter());
    }

    public void addVertex(Coordinate vertex) {
        if (vertex == null)
            throw new IllegalArgumentException("Vertex cannot be null");
        vertices.add(vertex);
        computeSurface();
    }

    public void removeVertex(Coordinate vertex) {
        if (vertices.size() <= 3)
            throw new IllegalStateException("Cannot remove vertex: zone must keep at least 3 vertices");
        vertices.remove(vertex);
        computeSurface();
    }

    public void addPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null");
        }

        patients.add(patient);
    }

    public int getPatientCount() {
        return patients.size();
    }

    public double getDensity() {
        if (surface == 0) {
            return 0;
        }

        return patients.size() / surface;
    }

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
