import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CommandLineApp {

    private MapManager manager;
    private Scanner scanner;

    public CommandLineApp() {
        manager = new MapManager();
        scanner = new Scanner(System.in);
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("=== Emergency Dispatcher ===");
            System.out.println("1 - Add hospital");
            System.out.println("2 - Remove hospital");
            System.out.println("3 - Move hospital");
            System.out.println("4 - Show Delaunay");
            System.out.println("5 - Show Voronoi zones");
            System.out.println("6 - Show hospitals");
            System.out.println("7 - Add patient");
            System.out.println("8 - Remove patient");
            System.out.println("9 - Move patient");
            System.out.println("10 - Show patients");
            System.out.println("11 - Show assigned hospital");
            System.out.println("12 - Add random patients");
            System.out.println("13 - Import hospitals from CSV");
            System.out.println("14 - Save map");
            System.out.println("15 - Load map");
            System.out.println("16 - Inspect triangle");
            System.out.println("17 - Inspect Voronoi zone");
            System.out.println("0 - Quit");

            System.out.print("Choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    addHospital();
                    break;

                case "2":
                    removeHospital();
                    break;

                case "3":
                    moveHospital();
                    break;

                case "4":
                    showDelaunay();
                    break;

                case "5":
                    showVoronoiZones();
                    break;

                case "6":
                    showHospitals();
                    break;

                case "7":
                    addPatient();
                    break;

                case "8":
                    removePatient();
                    break;

                case "9":
                    movePatient();
                    break;

                case "10":
                    showPatients();
                    break;

                case "11":
                    showAssignedHospital();
                    break;

                case "12":
                    addRandomPatients();
                    break;

                case "13":
                    importHospitalsFromCsv();
                    break;

                case "14":
                    saveMap();
                    break;

                case "15":
                    loadMap();
                    break;
                case "16":
                    inspectTriangle();
                    break;

                case "17":
                    inspectZone();
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }

        scanner.close();
    }

    private void addHospital() {

        System.out.print("Hospital id: ");
        String id = scanner.nextLine();
        if (id.isBlank()) {

            System.out.println("Hospital id cannot be empty.");

            return;

        }
        if (manager.getHospitals().containsKey(id)) {
            System.out.println("Hospital id already exists.");
            return;
        }

        System.out.print("Hospital name: ");
        String name = scanner.nextLine();

        Double latitude = readDouble("Latitude: ");
        if (latitude == null) {
            return;
        }

        Double longitude = readDouble("Longitude: ");
        if (longitude == null) {
            return;
        }

        Integer maxCapacity = readInteger("Max capacity: ");
        if (maxCapacity == null) {
            return;
        }

        if (maxCapacity <= 0) {
            System.out.println("Max capacity must be positive.");
            return;
        }

        Hospital hospital = new Hospital(
                id,
                name,
                new Coordinate(latitude, longitude),
                maxCapacity);

        addServicesToHospital(hospital);

        manager.addHospital(hospital);

        System.out.println("Hospital added.");
    }

    private void removeHospital() {

        System.out.print("Hospital id: ");
        String id = scanner.nextLine();

        if (!manager.getHospitals().containsKey(id)) {
            System.out.println("Hospital not found: " + id);
            return;
        }

        manager.removeHospital(id);

        System.out.println("Hospital removed.");
    }

    private void moveHospital() {

        System.out.print("Hospital id: ");
        String id = scanner.nextLine();

        if (!manager.getHospitals().containsKey(id)) {
            System.out.println("Hospital not found: " + id);
            return;
        }

        Double latitude = readDouble("New latitude: ");
        if (latitude == null) {
            return;
        }

        Double longitude = readDouble("New longitude: ");
        if (longitude == null) {
            return;
        }

        manager.moveHospital(
                id,
                new Coordinate(latitude, longitude));

        System.out.println("Hospital moved.");
    }

    private void addServicesToHospital(Hospital hospital) {
        boolean adding = true;

        while (adding) {
            System.out.println();
            System.out.println("Available services:");
            System.out.println("1 - GENERAL");
            System.out.println("2 - NEUROLOGY");
            System.out.println("3 - CARDIOLOGY");
            System.out.println("4 - PEDIATRICS");
            System.out.println("0 - Stop adding services");
            System.out.print("Choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    hospital.addService(HospitalServiceType.GENERAL);
                    break;
                case "2":
                    hospital.addService(HospitalServiceType.NEUROLOGY);
                    break;
                case "3":
                    hospital.addService(HospitalServiceType.CARDIOLOGY);
                    break;
                case "4":
                    hospital.addService(HospitalServiceType.PEDIATRICS);
                    break;
                case "0":
                    adding = false;
                    break;
                default:
                    System.out.println("Invalid service.");
            }
        }
    }

    private void addPatient() {

        System.out.print("Patient id: ");
        String id = scanner.nextLine();
        if (id.isBlank()) {

            System.out.println("Patient id cannot be empty.");

            return;

        }

        if (manager.getPatients().containsKey(id)) {
            System.out.println("Patient id already exists.");
            return;
        }

        System.out.print("Patient name: ");
        String name = scanner.nextLine();

        Double latitude = readDouble("Latitude: ");
        if (latitude == null) {
            return;
        }

        Double longitude = readDouble("Longitude: ");
        if (longitude == null) {
            return;
        }

        System.out.println("Required service:");
        System.out.println("1 - GENERAL");
        System.out.println("2 - NEUROLOGY");
        System.out.println("3 - CARDIOLOGY");
        System.out.println("4 - PEDIATRICS");

        String choice = scanner.nextLine();

        HospitalServiceType service;

        switch (choice) {
            case "1":
                service = HospitalServiceType.GENERAL;
                break;
            case "2":
                service = HospitalServiceType.NEUROLOGY;
                break;
            case "3":
                service = HospitalServiceType.CARDIOLOGY;
                break;
            case "4":
                service = HospitalServiceType.PEDIATRICS;
                break;
            default:
                System.out.println("Invalid service.");
                return;
        }

        Patient patient = new Patient(
                id,
                name,
                new Coordinate(latitude, longitude),
                service);

        manager.addPatient(patient);

        Hospital hospital = manager.getAssignedHospital(patient);

        if (hospital != null) {

            System.out.println("Patient added.");

            System.out.println("Assigned hospital: "
                    + hospital.getName());

        } else {

            System.out.println("Patient added.");
            System.out.println("No compatible hospital found.");
        }
    }

    private void removePatient() {

        System.out.print("Patient id: ");
        String id = scanner.nextLine();

        if (!manager.getPatients().containsKey(id)) {
            System.out.println("Patient not found: " + id);
            return;
        }

        manager.removePatient(id);

        System.out.println("Patient removed.");
    }

    private void movePatient() {

        System.out.print("Patient id: ");
        String id = scanner.nextLine();

        if (!manager.getPatients().containsKey(id)) {
            System.out.println("Patient not found: " + id);
            return;
        }

        Double latitude = readDouble("New latitude: ");
        if (latitude == null) {
            return;
        }

        Double longitude = readDouble("New longitude: ");
        if (longitude == null) {
            return;
        }

        manager.movePatient(
                id,
                new Coordinate(latitude, longitude));

        System.out.println("Patient moved.");
    }

    private void showPatients() {

        System.out.println();
        System.out.println("Patients:");

        for (Patient patient : manager.getPatients().values()) {
            System.out.println(patient);
        }
    }

    private void showAssignedHospital() {

        System.out.print("Patient id: ");
        String id = scanner.nextLine();

        Patient patient = manager.getPatients().get(id);

        if (patient == null) {
            System.out.println("Patient not found: " + id);
            return;
        }

        Hospital hospital = manager.getAssignedHospital(patient);

        if (hospital == null) {
            System.out.println("No compatible hospital found.");
            return;
        }

        System.out.println();
        System.out.println("Patient : "
                + patient.getName());

        System.out.println("Required service : "
                + patient.getRequiredService());

        System.out.println("Assigned hospital : "
                + hospital.getName());

        System.out.println("Capacity : "
                + hospital.getCurrentCapacity()
                + " / "
                + hospital.getMaxCapacity());
    }

    private void addRandomPatients() {

        System.out.print("Number of patients: ");

        String input = scanner.nextLine();

        try {

            int count = Integer.parseInt(input);

            if (count <= 0) {
                System.out.println("Number must be positive.");
                return;
            }

            for (int i = 0; i < count; i++) {

                int index = manager.getPatients().size() + i;

                String id = "R" + index;
                String name = "RandomPatient" + index;
                double latitude = Math.random() * 500;
                double longitude = Math.random() * 500;

                HospitalServiceType[] services = HospitalServiceType.values();

                HospitalServiceType service = services[(int) (Math.random() * services.length)];

                Patient patient = new Patient(
                        id,
                        name,
                        new Coordinate(latitude, longitude),
                        service);

                manager.addPatient(patient);
            }

            System.out.println(count + " random patients added.");

        } catch (NumberFormatException e) {

            System.out.println("Invalid number.");
        }
    }

    private void showDelaunay() {

        System.out.println();
        System.out.println("Delaunay triangles:");

        for (DelaunayTriangle triangle : manager.getTriangles()) {

            System.out.println(triangle);

            System.out.println(
                    "Surface: "
                            + triangle.getSurface());
            Coordinate center = triangle.getCircumcenter();

            System.out.println(
                    "Circumcenter: "
                            + center);

            Coordinate[] vertices = triangle.getVertices();

            Hospital hospitalA = findHospitalByPosition(vertices[0]);
            Hospital hospitalB = findHospitalByPosition(vertices[1]);
            Hospital hospitalC = findHospitalByPosition(vertices[2]);

            String nameA = hospitalA != null ? hospitalA.getName() : "A";
            String nameB = hospitalB != null ? hospitalB.getName() : "B";
            String nameC = hospitalC != null ? hospitalC.getName() : "C";

            System.out.println("Distance " + nameA + " - " + nameB + ": "
                    + triangle.getDistanceAB());

            System.out.println("Distance " + nameB + " - " + nameC + ": "
                    + triangle.getDistanceBC());

            System.out.println("Distance " + nameC + " - " + nameA + ": "
                    + triangle.getDistanceCA());

            int minPatients = Integer.MAX_VALUE;
            int maxPatients = 0;

            for (Hospital hospital : manager.getHospitals().values()) {

                if (triangle.containsHospital(hospital)) {

                    int patientCount = hospital.getCurrentCapacity();

                    System.out.println(
                            hospital.getName()
                                    + " patients: "
                                    + patientCount);

                    minPatients = Math.min(minPatients, patientCount);
                    maxPatients = Math.max(maxPatients, patientCount);
                }
            }

            System.out.println(
                    "Patient imbalance: "
                            + (maxPatients - minPatients));

            System.out.println();

        }
    }

    private void showVoronoiZones() {

        System.out.println();
        System.out.println("Voronoi zones:");

        for (VoronoiZone zone : manager.getZones()) {

            System.out.println(zone);

            System.out.println(
                    "Patients: "
                            + zone.getPatientCount());

            System.out.println(
                    "Density: "
                            + zone.getDensity());

            System.out.println("Min distance: " + zone.getMinDistanceToHospital());
            System.out.println("Max distance: " + zone.getMaxDistanceToHospital());

            System.out.println(
                    "Average distance: "
                            + zone.getAverageDistanceToHospital());

            System.out.println();
        }
    }

    private void showHospitals() {

        System.out.println();
        System.out.println("Hospitals:");

        for (Hospital hospital : manager.getHospitals().values()) {
            System.out.println(hospital);
        }
    }

    private void importHospitalsFromCsv() {

        System.out.print("CSV file path: ");
        String filePath = scanner.nextLine();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                if (data.length < 6) {
                    continue;
                }

                String id = data[0].trim();
                String name = data[1].trim();

                double latitude = Double.parseDouble(data[2].trim());

                double longitude = Double.parseDouble(data[3].trim());

                int maxCapacity = Integer.parseInt(data[4].trim());
                HospitalServiceType service = HospitalServiceType.valueOf(
                        data[5].trim().toUpperCase());

                Hospital hospital = new Hospital(
                        id,
                        name,
                        new Coordinate(
                                latitude,
                                longitude),
                        maxCapacity);

                hospital.addService(service);

                manager.addHospital(hospital);
            }

            System.out.println(
                    "Hospitals imported successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Unable to read file.");

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid data inside CSV.");
        }
    }

    private void saveMap() {

        System.out.print("File name: ");
        String fileName = scanner.nextLine();

        try {

            MapPersistenceService persistence = new MapPersistenceService();

            persistence.saveMap(
                    manager,
                    fileName);

            System.out.println("Map saved.");

        } catch (Exception e) {

            System.out.println("Unable to save map.");
        }
    }

    private void loadMap() {

        System.out.print("File name: ");
        String fileName = scanner.nextLine();

        try {

            MapPersistenceService persistence = new MapPersistenceService();

            manager = persistence.loadMap(
                    fileName);

            manager.initializeAfterLoading();

            System.out.println("Map loaded.");

        } catch (Exception e) {

            System.out.println("Unable to load map.");
        }
    }

    private Double readDouble(String message) {
        System.out.print(message);

        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }

    private Integer readInteger(String message) {
        System.out.print(message);

        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid integer.");
            return null;
        }
    }

    private Hospital findHospitalByPosition(Coordinate position) {

        for (Hospital hospital : manager.getHospitals().values()) {
            if (hospital.getPosition().equals(position)) {
                return hospital;
            }
        }

        return null;
    }

    private void inspectTriangle() {

        List<DelaunayTriangle> triangles = manager.getTriangles();

        if (triangles.isEmpty()) {
            System.out.println("No triangle available.");
            return;
        }

        for (int i = 0; i < triangles.size(); i++) {
            System.out.println(i + " - " + triangles.get(i));
        }

        Integer index = readInteger("Triangle index: ");

        if (index == null || index < 0 || index >= triangles.size()) {
            System.out.println("Invalid triangle index.");
            return;
        }

        DelaunayTriangle triangle = triangles.get(index);

        System.out.println();
        System.out.println(triangle);
        System.out.println("Surface: " + triangle.getSurface());
        System.out.println("Circumcenter: " + triangle.getCircumcenter());

        System.out.println("Distance AB: " + triangle.getDistanceAB());
        System.out.println("Distance BC: " + triangle.getDistanceBC());
        System.out.println("Distance CA: " + triangle.getDistanceCA());
    }

    private void inspectZone() {

        List<VoronoiZone> zones = manager.getZones();

        if (zones.isEmpty()) {
            System.out.println("No Voronoi zone available.");
            return;
        }

        for (int i = 0; i < zones.size(); i++) {
            System.out.println(i + " - " + zones.get(i).getHospital().getName());
        }

        Integer index = readInteger("Zone index: ");

        if (index == null || index < 0 || index >= zones.size()) {
            System.out.println("Invalid zone index.");
            return;
        }

        VoronoiZone zone = zones.get(index);

        System.out.println();
        System.out.println(zone);
        System.out.println("Surface: " + zone.getSurface());
        System.out.println("Patients: " + zone.getPatientCount());
        System.out.println("Density: " + zone.getDensity());
        System.out.println("Min distance: " + zone.getMinDistanceToHospital());
        System.out.println("Max distance: " + zone.getMaxDistanceToHospital());
        System.out.println("Average distance: " + zone.getAverageDistanceToHospital());
    }

}