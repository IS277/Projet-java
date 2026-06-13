import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


import java.util.*;
import java.util.function.Consumer;

/**
 * JavaFX view providing an interactive patient simulation panel.
 *
 * <p>Allows the dispatcher to create individual or batches of random patients,
 * observe real-time assignment results in a log and monitor hospital saturation.
 * A callback ({@link #setOnPatientPlaced}) lets {@link MapView} synchronise its state.</p>
 *
 * @author Maissa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class SimulationView {

    private final List<Hospital> hospitals;
    private final TextArea log = new TextArea();
    private int pid = 1;
    private final Random rng = new Random();
    private Consumer<Patient> onPatientPlaced;

    /**
     * Registers a callback invoked each time a patient is created via this view.
     *
     * @param cb consumer receiving the newly created {@link Patient}
     */
    public void setOnPatientPlaced(Consumer<Patient> cb) { this.onPatientPlaced = cb; }

    /**
     * Creates a simulation view bound to the given list of hospitals.
     *
     * @param hospitals hospitals against which patients will be assigned
     */
    public SimulationView(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    /**
     * Builds and displays the simulation window.
     */
    public void show() {
        log.setEditable(false);
        log.setPrefHeight(320);
        log.setStyle("-fx-font-family: monospace; -fx-font-size: 11px;");

        ComboBox<HospitalServiceType> svcCombo = new ComboBox<>();
        svcCombo.getItems().addAll(HospitalServiceType.values());
        svcCombo.setValue(HospitalServiceType.GENERAL);
        svcCombo.setMaxWidth(Double.MAX_VALUE);

        Button btnOne    = new Button("+ 1 patient");
        Button btnFive   = new Button("+ 5 aléatoires");
        Button btnClear  = new Button("Effacer");
        btnOne.setMaxWidth(Double.MAX_VALUE);
        btnFive.setMaxWidth(Double.MAX_VALUE);
        btnClear.setMaxWidth(Double.MAX_VALUE);

        btnOne.setOnAction(e -> addPatient(svcCombo.getValue()));
        btnFive.setOnAction(e -> { for (int i = 0; i < 5; i++) addPatient(randomService()); });
        btnClear.setOnAction(e -> log.clear());

        // Hospital status panel
        VBox statusBox = new VBox(6);
        statusBox.setPadding(new Insets(10));
        statusBox.setPrefWidth(210);
        statusBox.getChildren().add(bold("État des hôpitaux"));
        for (Hospital h : hospitals) statusBox.getChildren().add(hospitalStatus(h));

        VBox controls = new VBox(8, bold("Service"), svcCombo, btnOne, btnFive, btnClear);
        controls.setPadding(new Insets(10));
        controls.setPrefWidth(170);

        BorderPane root = new BorderPane();
        root.setCenter(new VBox(6, bold("Journal d'assignation"), log) {{ setPadding(new Insets(10)); }});
        root.setLeft(statusBox);
        root.setRight(controls);

        Stage stage = new Stage();
        stage.setTitle("Simulation");
        stage.setScene(new Scene(root, 720, 430));
        stage.show();
    }

    private void addPatient(HospitalServiceType svc) {
        try {
            Patient p = new Patient("SIM" + pid, "Sim-" + pid,
                    new Coordinate(5 + rng.nextDouble() * 150, 5 + rng.nextDouble() * 230), svc);
            pid++;
            Hospital best = AssignmentService.findBestHospital(p, hospitals);
            if (best != null) {
                log.appendText(String.format("%-12s → %-18s  dist=%.1f  sat=%.0f%%%n",
                        p.getName(), best.getName(),
                        p.getPosition().distanceTo(best.getPosition()),
                        best.getSaturationRate() * 100));
            } else {
                log.appendText(p.getName() + " → aucun hôpital disponible pour " + svc.name() + "\n");
            }
            if (onPatientPlaced != null) onPatientPlaced.accept(p);
        } catch (Exception ex) { log.appendText("Error: " + ex.getMessage() + "\n"); }
    }

    private Label hospitalStatus(Hospital h) {
        double sat = h.getSaturationRate() * 100;
        Label l = new Label(String.format("%-18s %d/%d (%.0f%%)",
                h.getName(), h.getCurrentCapacity(), h.getMaxCapacity(), sat));
        l.setFont(Font.font(null, FontWeight.NORMAL, 11));
        l.setTextFill(sat >= 80 ? Color.RED : sat >= 50 ? Color.DARKORANGE : Color.DARKGREEN);
        return l;
    }

    private HospitalServiceType randomService() {
        HospitalServiceType[] vals = HospitalServiceType.values();
        return vals[rng.nextInt(vals.length)];
    }

    private Label bold(String t) {
        Label l = new Label(t);
        l.setFont(Font.font(null, FontWeight.BOLD, 13));
        return l;
    }
}
