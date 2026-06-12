import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

public class ZoneDetailView {

    public void show(VoronoiZone zone, List<VoronoiZone> allZones) {
        Hospital h = zone.getHospital();

        // --- En-tête ---
        Label title = new Label("Zone — " + h.getName());
        title.setFont(Font.font(null, FontWeight.BOLD, 18));

        double sat = h.getSaturationRate() * 100;
        Label satLabel = new Label(String.format("Saturation hôpital : %d/%d  (%.0f%%)",
                h.getCurrentCapacity(), h.getMaxCapacity(), sat));
        satLabel.setTextFill(sat >= 80 ? Color.RED : sat >= 50 ? Color.ORANGE : Color.GREEN);

        // --- Géométrie ---
        Label secGeo = section("Géométrie");
        Label surface  = row("Surface",   String.format("%.2f u²", zone.getSurface()));
        Label perim    = row("Périmètre", String.format("%.2f u",  zone.getPerimeter()));
        Label density  = row("Densité",   String.format("%.4f patients/u²", zone.getDensity()));

        // --- Patients ---
        Label secPat = section("Patients");
        int np = zone.getPatientCount();
        double avgAll = allZones.stream()
                .mapToInt(VoronoiZone::getPatientCount).average().orElse(0);
        String vsAvg = np > avgAll ? "▲ au-dessus" : np < avgAll ? "▼ en-dessous" : "= moyenne";
        Label nbPat   = row("Nombre",        np + "  (" + vsAvg + " de la moyenne " + String.format("%.1f", avgAll) + ")");
        Label distMin = row("Dist. min",     fmt1(zone.getMinDistanceToHospital()) + " u");
        Label distMoy = row("Dist. moyenne", fmt1(zone.getAverageDistanceToHospital()) + " u");
        Label distMax = row("Dist. max",     fmt1(zone.getMaxDistanceToHospital()) + " u");
        Label distSd  = row("Écart-type",    fmt1(zone.getStdDevDistanceToHospital()) + " u");

        // --- Répartition par service ---
        Label secSvc = section("Répartition par service");
        VBox svcBox = new VBox(3);
        Map<HospitalServiceType, Long> bySvc = zone.getPatientsByService();
        if (bySvc.isEmpty()) {
            svcBox.getChildren().add(new Label("  Aucun patient"));
        } else {
            for (Map.Entry<HospitalServiceType, Long> e : bySvc.entrySet()) {
                double pct = np > 0 ? 100.0 * e.getValue() / np : 0;
                svcBox.getChildren().add(row("  " + e.getKey().name(),
                        e.getValue() + "  (" + String.format("%.0f%%", pct) + ")"));
            }
        }

        // --- Services de l'hôpital ---
        Label secHosp = section("Hôpital");
        String svcs = h.getServices().stream()
                .map(Enum::name).reduce((a, b) -> a + ", " + b).orElse("—");
        Label hospSvcs = row("Services", svcs);
        Label hospPos  = row("Position", String.format("lat=%.1f  lon=%.1f",
                h.getPosition().getLatitude(), h.getPosition().getLongitude()));

        // --- Bouton ---
        Button btnClose = new Button("Fermer");
        Stage stage = new Stage();
        btnClose.setOnAction(e -> stage.close());
        btnClose.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(8,
                title, satLabel, new Separator(),
                secGeo, surface, perim, density, new Separator(),
                secPat, nbPat, distMin, distMoy, distMax, distSd, new Separator(),
                secSvc, svcBox, new Separator(),
                secHosp, hospSvcs, hospPos, new Separator(),
                btnClose);
        root.setPadding(new Insets(20));

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        stage.setTitle("Zone — " + h.getName());
        stage.setScene(new Scene(scroll, 360, 520));
        stage.setResizable(false);
        stage.show();
    }

    private Label section(String text) {
        Label l = new Label(text);
        l.setFont(Font.font(null, FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#1e40af"));
        return l;
    }

    private Label row(String key, String value) {
        return new Label(String.format("  %-18s %s", key + " :", value));
    }

    private String fmt1(double v) {
        return v == 0 ? "—" : String.format("%.1f", v);
    }
}
