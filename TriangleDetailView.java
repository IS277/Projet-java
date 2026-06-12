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

public class TriangleDetailView {

    public void show(DelaunayTriangle t, Map<Patient, Hospital> assignments) {
        Coordinate[] v = t.getVertices();
        Coordinate cc  = t.getCircumcenter();

        Label title = new Label("Triangle de Delaunay");
        title.setFont(Font.font(null, FontWeight.BOLD, 18));

        // --- Géométrie ---
        Label secGeo = section("Géométrie");
        double a = v[0].distanceTo(v[1]);
        double b = v[1].distanceTo(v[2]);
        double c = v[2].distanceTo(v[0]);
        double s = (a + b + c) / 2;
        double radius = (a * b * c) / (4 * t.getSurface());

        Label surf   = row("Surface",           String.format("%.2f u²", t.getSurface()));
        Label perim  = row("Périmètre",          String.format("%.2f u",  a + b + c));
        Label aretes = row("Arêtes",             String.format("%.1f / %.1f / %.1f", a, b, c));
        Label circR  = row("Rayon circonscrit",  String.format("%.2f u",  radius));
        Label circC  = row("Circumcentre",       String.format("lat=%.1f  lon=%.1f",
                            cc.getLatitude(), cc.getLongitude()));

        // compacité = ratio surface / surface du cercle circonscrit
        double compactness = t.getSurface() / (Math.PI * radius * radius);
        Label compact = row("Compacité",         String.format("%.3f  (1=équilatéral)", compactness));

        // --- Sommets / Hôpitaux ---
        Label secH = section("Hôpitaux sommets");
        VBox hBox = new VBox(4);
        long minPat = Long.MAX_VALUE, maxPat = Long.MIN_VALUE;
        Hospital mostLoaded = null, leastLoaded = null;
        for (Coordinate vi : v) {
            Hospital h = findHospital(vi, assignments);
            if (h == null) {
                hBox.getChildren().add(new Label("  ? — position inconnue"));
                continue;
            }
            long cnt = assignments.entrySet().stream()
                    .filter(e -> e.getValue() == h).count();
            double sat = h.getSaturationRate() * 100;
            Label hl = new Label(String.format("  %-10s  %d patients  sat. %.0f%%",
                    h.getName(), cnt, sat));
            hl.setTextFill(sat >= 80 ? Color.RED : sat >= 50 ? Color.ORANGE : Color.GREEN);
            hBox.getChildren().add(hl);
            if (cnt > maxPat) { maxPat = cnt; mostLoaded  = h; }
            if (cnt < minPat) { minPat = cnt; leastLoaded = h; }
        }

        // --- Déséquilibre ---
        Label secD = section("Déséquilibre de charge");
        long imbalance = (maxPat == Long.MIN_VALUE) ? 0 : maxPat - minPat;
        String imbalanceStr = imbalance == 0 ? "Équilibré" :
                String.format("%d patients d'écart", imbalance);
        Label lblImbalance = row("Écart max/min", imbalanceStr);
        lblImbalance.setTextFill(imbalance == 0 ? Color.GREEN
                               : imbalance <= 5  ? Color.ORANGE : Color.RED);
        Label lblMost  = mostLoaded  != null ? row("Plus chargé",  mostLoaded.getName()
                + String.format(" (%d patients, %.0f%%)", maxPat, mostLoaded.getSaturationRate()*100)) : new Label();
        Label lblLeast = leastLoaded != null && leastLoaded != mostLoaded
                ? row("Moins chargé", leastLoaded.getName()
                + String.format(" (%d patients, %.0f%%)", minPat, leastLoaded.getSaturationRate()*100)) : new Label();
        String advice = imbalance > 10 ? "→ Transfert de patients conseillé vers " + (leastLoaded != null ? leastLoaded.getName() : "—")
                      : imbalance > 5  ? "→ Déséquilibre modéré à surveiller"
                      : "→ Répartition satisfaisante";
        Label lblAdvice = new Label("  " + advice);
        lblAdvice.setTextFill(imbalance > 10 ? Color.RED : imbalance > 5 ? Color.ORANGE : Color.GREEN);
        lblAdvice.setWrapText(true);

        // --- Qualité géométrique ---
        Label secQ = section("Qualité géométrique");
        double minAngle = minAngle(v);
        double maxAngle = 180 - minAngle - midAngle(v);
        Label angMin = row("Angle min",  String.format("%.1f°", minAngle));
        Label angMax = row("Angle max",  String.format("%.1f°", maxAngle));
        String quality = minAngle < 20 ? "Mauvaise (triangle aplati)"
                       : minAngle < 30 ? "Acceptable"
                       : "Bonne";
        Label qual = row("Qualité",     quality);
        qual.setTextFill(minAngle < 20 ? Color.RED : minAngle < 30 ? Color.ORANGE : Color.GREEN);

        Button btnClose = new Button("Fermer");
        Stage stage = new Stage();
        btnClose.setOnAction(e -> stage.close());
        btnClose.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(8,
                title, new Separator(),
                secGeo, surf, perim, aretes, circR, circC, compact, new Separator(),
                secH, hBox, new Separator(),
                secD, lblImbalance, lblMost, lblLeast, lblAdvice, new Separator(),
                secQ, angMin, angMax, qual, new Separator(),
                btnClose);
        root.setPadding(new Insets(20));

        stage.setTitle("Triangle de Delaunay");
        stage.setScene(new Scene(root, 360, 540));
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
        return new Label(String.format("  %-20s %s", key + " :", value));
    }

    private Hospital findHospital(Coordinate pos, Map<Patient, Hospital> assignments) {
        return assignments.values().stream()
                .distinct()
                .filter(h -> h.getPosition().equals(pos))
                .findFirst().orElse(null);
    }

    private double minAngle(Coordinate[] v) {
        double a = v[0].distanceTo(v[1]);
        double b = v[1].distanceTo(v[2]);
        double c = v[2].distanceTo(v[0]);
        double A = Math.toDegrees(Math.acos((b*b + c*c - a*a) / (2*b*c)));
        double B = Math.toDegrees(Math.acos((a*a + c*c - b*b) / (2*a*c)));
        double C = 180 - A - B;
        return Math.min(A, Math.min(B, C));
    }

    private double midAngle(Coordinate[] v) {
        double a = v[0].distanceTo(v[1]);
        double b = v[1].distanceTo(v[2]);
        double c = v[2].distanceTo(v[0]);
        double A = Math.toDegrees(Math.acos((b*b + c*c - a*a) / (2*b*c)));
        double B = Math.toDegrees(Math.acos((a*a + c*c - b*b) / (2*a*c)));
        double C = 180 - A - B;
        double min = Math.min(A, Math.min(B, C));
        double max = Math.max(A, Math.max(B, C));
        return A + B + C - min - max;
    }
}
