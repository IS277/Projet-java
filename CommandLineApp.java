import java.util.ArrayList;
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
            System.out.println("11 - Assign patient");
            System.out.println("12 - Add random patients");
            System.out.println("13 - Import hospitals from CSV");
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
                    assignPatient();
                    break;

                case "12":
                    addRandomPatients();
                    break;

                case "13":
                    importHospitalsFromCsv();
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

        System.out.print("Hospital name: ");
        String name = scanner.nextLine();

        System.out.print("Latitude: ");
        double latitude = Double.parseDouble(scanner.nextLine());

        System.out.print("Longitude: ");
        double longitude = Double.parseDouble(scanner.nextLine());

        System.out.print("Max capacity: ");
        int maxCapacity = Integer.parseInt(scanner.nextLine());

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

        System.out.print("New latitude: ");
        double latitude = Double.parseDouble(scanner.nextLine());

        System.out.print("New longitude: ");
        double longitude = Double.parseDouble(scanner.nextLine());

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

        System.out.print("Patient name: ");
        String name = scanner.nextLine();

        System.out.print("Latitude: ");
        double latitude = Double.parseDouble(scanner.nextLine());

        System.out.print("Longitude: ");
        double longitude = Double.parseDouble(scanner.nextLine());

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

        System.out.println("Patient added.");
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

        System.out.print("New latitude: ");
        double latitude = Double.parseDouble(scanner.nextLine());

        System.out.print("New longitude: ");
        double longitude = Double.parseDouble(scanner.nextLine());

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

    private void assignPatient() {

        System.out.print("Patient id: ");
        String id = scanner.nextLine();

        Patient patient = manager.getPatients().get(id);

        if (patient == null) {
            System.out.println("Patient not found: " + id);
            return;
        }

        AssignmentService assignmentService = new AssignmentService();

        Hospital hospital = assignmentService.findBestHospital(
                patient,
                new ArrayList<>(
                        manager.getHospitals().values()));

        if (hospital == null) {
            System.out.println("No compatible hospital found.");
            return;
        }

        hospital.admitPatient();

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

                Patient patient = new Patient(
                        id,
                        name,
                        new Coordinate(latitude, longitude),
                        HospitalServiceType.GENERAL);

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

                if (data.length < 5) {
                    continue;
                }

                String id = data[0].trim();
                String name = data[1].trim();

                double latitude = Double.parseDouble(data[2].trim());

                double longitude = Double.parseDouble(data[3].trim());

                int maxCapacity = Integer.parseInt(data[4].trim());

                Hospital hospital = new Hospital(
                        id,
                        name,
                        new Coordinate(
                                latitude,
                                longitude),
                        maxCapacity);

                hospital.addService(
                        HospitalServiceType.GENERAL);

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
}