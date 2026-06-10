import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;

public class HospitalDetailView {

    public void show(Hospital h, List<Patient> assigned, VoronoiZone zone) {

        Label name = new Label(h.getName());
        name.setFont(Font.font(null, FontWeight.BOLD, 18));

        double sat = h.getSaturationRate() * 100;
        Label capacity = new Label(String.format("Capacité : %d / %d  (%.0f%%)",
                h.getCurrentCapacity(), h.getMaxCapacity(), sat));
        capacity.setTextFill(sat >= 80 ? Color.RED : sat >= 50 ? Color.ORANGE : Color.GREEN);

        Label position = new Label(String.format("Position : lat=%.1f  lon=%.1f",
                h.getPosition().getLatitude(), h.getPosition().getLongitude()));

        String svcs = h.getServices().stream()
                .map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("—");
        Label services = new Label("Services : " + svcs);

        Label zoneLabel = new Label(zone != null
                ? String.format("Surface zone : %.1f u²", zone.getSurface())
                : "Zone : non calculée");

        Label pTitle = new Label("Patients assignés (" + assigned.size() + ") :");
        pTitle.setFont(Font.font(null, FontWeight.BOLD, 13));

        ListView<String> list = new ListView<>();
        assigned.forEach(p -> list.getItems().add(
                p.getName() + "  —  " + p.getRequiredService().name()));
        list.setPrefHeight(130);

        Button btnClose = new Button("Fermer");
        Stage stage = new Stage();
        btnClose.setOnAction(e -> stage.close());

        VBox root = new VBox(10, name, new Separator(),
                capacity, position, services, zoneLabel,
                new Separator(), pTitle, list, btnClose);
        root.setPadding(new Insets(20));

        stage.setTitle("Détails — " + h.getName());
        stage.setScene(new Scene(root, 340, 450));
        stage.setResizable(false);
        stage.show();
    }
}
