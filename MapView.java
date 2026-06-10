import javafx.event.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.input.MouseButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class MapView {

    private static final int    W = 700, H = 550;
    private static final double SCALE = 2.8, MARGIN = 40.0;

    // Abstraction
    private final List<Hospital>         hospitals   = new ArrayList<>();
    private final List<Patient>          patients    = new ArrayList<>();
    private final Map<Patient, Hospital> assignments = new LinkedHashMap<>();
    private List<DelaunayTriangle> triangles = new ArrayList<>();
    private List<VoronoiZone>      zones     = new ArrayList<>();
    private List<VoronoiEdge>      edges     = new ArrayList<>();

    // Contrôle
    private Hospital selectedHospital;
    private boolean  addingHospital, placingPatient;
    private int      hid = 6, pid = 1;
    private Hospital dragged = null;
    private boolean  wasDragged = false;
    private double   zoom = SCALE, panX = MARGIN, panY = H - MARGIN;
    private boolean  panning = false;
    private double   lastMX, lastMY;

    // Présentation
    private Stage     stage;
    private Canvas    canvas;
    private Button    btnAdd, btnRemove, btnPlace;
    private TextField hospName, patientName;
    private ComboBox<HospitalServiceType> svcCombo;
    private Label     infoLabel, resultLabel;
    private CheckBox  cbDelaunay = new CheckBox("Delaunay");
    private CheckBox  cbVoronoi  = new CheckBox("Voronoï");

    public void show() {
        initHospitals();
        computeGeometry();

        canvas = new Canvas(W, H);
        cbDelaunay.setSelected(true); cbVoronoi.setSelected(true);
        cbDelaunay.setOnAction(e -> drawMap()); cbVoronoi.setOnAction(e -> drawMap());

        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) { panning = true; }
            else { dragged = hospitals.stream()
                .filter(h -> Math.hypot(e.getX()-sx(h), e.getY()-sy(h)) < 14)
                .findFirst().orElse(null); }
            lastMX = e.getX(); lastMY = e.getY(); wasDragged = false;
        });
        canvas.setOnMouseDragged(e -> {
            wasDragged = true;
            if (panning) {
                panX += e.getX() - lastMX; panY += e.getY() - lastMY; drawMap();
            } else if (dragged != null) {
                dragged.updatePosition(new Coordinate(screenToLat(e.getY()), screenToLon(e.getX())));
                computeGeometry(); drawMap();
            }
            lastMX = e.getX(); lastMY = e.getY();
        });
        canvas.setOnMouseReleased(e -> { dragged = null; panning = false; });
        canvas.setOnMouseClicked(e -> { if (!wasDragged) handleClick(e.getX(), e.getY()); });
        canvas.setOnScroll(e -> {
            double f = e.getDeltaY() > 0 ? 1.15 : 1 / 1.15;
            panX = e.getX() - (e.getX() - panX) * f;
            panY = e.getY() + (panY - e.getY()) * f;
            zoom *= f; drawMap();
        });

        hospName    = new TextField(); hospName.setPromptText("Hospital name...");
        patientName = new TextField(); patientName.setPromptText("Patient name...");
        svcCombo    = new ComboBox<>();
        svcCombo.getItems().addAll(HospitalServiceType.values());
        svcCombo.setValue(HospitalServiceType.GENERAL);
        svcCombo.setMaxWidth(Double.MAX_VALUE);
        infoLabel   = new Label("Click a hospital."); infoLabel.setWrapText(true);
        resultLabel = new Label("—");                 resultLabel.setWrapText(true);

        btnAdd = new Button("Add Hospital");
        btnAdd.setOnAction(e -> {
            addingHospital = !addingHospital; placingPatient = false;
            btnAdd.setText(addingHospital ? "Click map…" : "Add Hospital");
            canvas.setCursor(addingHospital ? Cursor.CROSSHAIR : Cursor.DEFAULT);
        });
        btnRemove = new Button("Remove"); btnRemove.setDisable(true);
        btnRemove.setOnAction(e -> {
            hospitals.remove(selectedHospital); selectedHospital = null;
            computeGeometry(); btnRemove.setDisable(true);
            infoLabel.setText("Click a hospital."); drawMap();
        });
        btnPlace = new Button("Place Patient");
        btnPlace.setOnAction(e -> {
            placingPatient = true; addingHospital = false;
            btnPlace.setText("Click map…"); canvas.setCursor(Cursor.CROSSHAIR);
        });

        // Layout : BorderPane (root) > VBox (panel) > HBox (lignes de boutons)
        VBox panel = new VBox(10,
            bold("View"), new HBox(10, cbDelaunay, cbVoronoi),
            sep(), bold("File"),     row(btn("Import CSV", e -> importCSV()),
                                  btn("Save",       e -> saveMap()),
                                  btn("Load",       e -> loadMap())),
            sep(), bold("Hospital"), hospName, row(btnAdd, btnRemove), infoLabel,
            sep(), bold("Patient"),  patientName, svcCombo,
                                     row(btnPlace,
                                         btn("+5 Random", e -> addRandomPatients(5)),
                                         btn("Clear", e -> { patients.clear(); assignments.clear();
                                                              resultLabel.setText("—"); drawMap(); })),
            sep(), bold("Result"), resultLabel,
            sep(), btn("Simulation", e -> new SimulationView(new ArrayList<>(hospitals)).show()),
            btn("Reset view",   e -> { zoom = SCALE; panX = MARGIN; panY = H - MARGIN; drawMap(); }));
        panel.setPadding(new Insets(16)); panel.setPrefWidth(260);

        BorderPane root = new BorderPane();
        root.setLeft(canvas); root.setRight(panel);

        stage = new Stage();
        stage.setTitle("Emergency Dispatcher");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        drawMap();
    }

    // Gestionnaire de clic Canvas — 3 modes : ajout hôpital / placement patient / sélection
    private void handleClick(double mx, double my) {
        if (addingHospital) {
            String name = hospName.getText().isBlank() ? "H" + hid : hospName.getText();
            Hospital h  = new Hospital("H" + hid++, name,
                    new Coordinate(screenToLat(my), screenToLon(mx)), 100);
            h.addService(HospitalServiceType.GENERAL);
            hospitals.add(h); computeGeometry();
            addingHospital = false; btnAdd.setText("Add Hospital"); canvas.setCursor(Cursor.DEFAULT);
        } else if (placingPatient) {
            placePatient(patientName.getText(), screenToLat(my), screenToLon(mx));
            placingPatient = false; btnPlace.setText("Place Patient"); canvas.setCursor(Cursor.DEFAULT);
        } else {
            selectedHospital = hospitals.stream()
                    .filter(h -> Math.hypot(mx - sx(h), my - sy(h)) < 14)
                    .findFirst().orElse(null);
            btnRemove.setDisable(selectedHospital == null);
            if (selectedHospital != null) {
                infoLabel.setText(hospitalInfo(selectedHospital));
                List<Patient> assigned = new ArrayList<>();
                assignments.forEach((p, h) -> { if (h == selectedHospital) assigned.add(p); });
                VoronoiZone zone = zones.stream()
                    .filter(z -> z.getHospital() == selectedHospital).findFirst().orElse(null);
                new HospitalDetailView().show(selectedHospital, assigned, zone);
            } else {
                Coordinate wp = new Coordinate(screenToLat(my), screenToLon(mx));
                VoronoiZone z = zones.stream().filter(zn -> zn.contains(wp)).findFirst().orElse(null);
                if (z != null) {
                    infoLabel.setText(zoneInfo(z));
                } else {
                    DelaunayTriangle t = findTriangle(wp);
                    infoLabel.setText(t != null ? triangleInfo(t) : "Click a hospital or zone.");
                }
            }
        }
        drawMap();
    }

    // --- Abstraction ---

    private void initHospitals() {
        addH("H1","Paris Hospital",   10,  20, 100, 45, "NEUROLOGY,GENERAL");
        addH("H2","City Hospital",   100, 200,  50, 38, "CARDIOLOGY");
        addH("H3","North Hospital",   80,  40,  80, 20, "GENERAL");
        addH("H4","West Hospital",    30, 150,  70, 60, "GENERAL,PEDIATRICS");
        addH("H5","South Hospital",  150,  80,  90, 72, "CARDIOLOGY,NEUROLOGY");
    }

    private void addH(String id, String name, double lat, double lon, int cap, int cur, String svcs) {
        Hospital h = new Hospital(id, name, new Coordinate(lat, lon), cap);
        for (String s : svcs.split(",")) h.addService(parseService(s));
        h.updateCapacity(cur);
        hospitals.add(h);
    }

    private void computeGeometry() {
        if (hospitals.size() < 3) {
            triangles = new ArrayList<>(); edges = new ArrayList<>(); zones = new ArrayList<>(); return;
        }
        triangles = new Delaunay().triangulate(hospitals);
        VoronoiService vs = new VoronoiService();
        edges = vs.getVoronoiEdges(triangles);
        zones = vs.generateZones(hospitals, patients, triangles);
    }

    // Place un patient et l'assigne immédiatement au meilleur hôpital (AssignmentService)
    private void placePatient(String rawName, double lat, double lon) {
        int id = pid++;
        Patient p = new Patient("P"+id, rawName.isBlank() ? "P"+id : rawName,
                new Coordinate(lat, lon), svcCombo.getValue());
        patients.add(p);
        Hospital best = new AssignmentService().findBestHospital(p, hospitals);
        if (best != null) {
            assignments.put(p, best);
            resultLabel.setText(String.format("%s%n%.1f units — %.0f%% full",
                    best.getName(), p.getPosition().distanceTo(best.getPosition()),
                    best.getSaturationRate() * 100));
            resultLabel.setStyle("-fx-text-fill:#198754;");
        } else {
            resultLabel.setText("No hospital for this service."); resultLabel.setStyle("-fx-text-fill:#dc3545;");
        }
        drawMap();
    }

    // --- Contrôle : fichiers ---

    // Import CSV — FileChooser + BufferedReader (format : name,lat,lon,cap[,SERVICE])
    private void importCSV() {
        File f = picker("Import Hospitals","*.csv").showOpenDialog(stage);
        if (f == null) return;
        int n = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            for (String line; (line = br.readLine()) != null; ) {
                String[] p = line.trim().split(",");
                if (p.length < 4 || line.startsWith("#")) continue;
                try {
                    Hospital h = new Hospital("H"+hid++, p[0].trim(),
                            new Coordinate(Double.parseDouble(p[1]), Double.parseDouble(p[2])),
                            Integer.parseInt(p[3]));
                    h.addService(p.length >= 5 ? parseService(p[4]) : HospitalServiceType.GENERAL);
                    hospitals.add(h); n++;
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException ex) { alert(ex.getMessage()); return; }
        computeGeometry(); drawMap(); alert(n + " hospital(s) imported.");
    }

    // Sauvegarde binaire — ObjectOutputStream (Serializable requis sur Hospital, Patient, Coordinate)
    private void saveMap() {
        File f = picker("Save Map","*.map").showSaveDialog(stage);
        if (f == null) return;
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(new ArrayList<>(hospitals));
            out.writeObject(new ArrayList<>(patients));
            alert("Map saved.");
        } catch (IOException ex) { alert("Error: " + ex.getMessage()); }
    }

    // Chargement binaire — ObjectInputStream
    @SuppressWarnings("unchecked")
    private void loadMap() {
        File f = picker("Load Map","*.map").showOpenDialog(stage);
        if (f == null) return;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            hospitals.clear(); hospitals.addAll((List<Hospital>) in.readObject());
            patients.clear();  patients.addAll((List<Patient>)  in.readObject());
            assignments.clear(); selectedHospital = null;
            computeGeometry(); drawMap(); alert("Map loaded.");
        } catch (Exception ex) { alert("Error: " + ex.getMessage()); }
    }

    private void addRandomPatients(int n) {
        Random rng = new Random();
        for (int i = 0; i < n; i++)
            placePatient("", 5 + rng.nextDouble() * 150, 5 + rng.nextDouble() * 230);
    }

    // --- Présentation : rendu Canvas ---

    private void drawMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, W, H);
        gc.setFill(Color.web("#e8f4f8")); gc.fillRect(0, 0, W, H);

        // Zones Voronoï (fillPolygon exige des sommets triés par angle)
        for (VoronoiZone z : zones) {
            List<Coordinate> v = sortedByAngle(z.getVertices(), z.getCenter());
            if (v.size() < 3) continue;
            double[] xs = v.stream().mapToDouble(this::sx).toArray();
            double[] ys = v.stream().mapToDouble(this::sy).toArray();
            Color c = satColor(z.getHospital().getSaturationRate());
            gc.setFill(c.deriveColor(0,1,1,0.18)); gc.fillPolygon(xs,ys,v.size());
            gc.setStroke(c.deriveColor(0,1,0.8,0.5)); gc.setLineWidth(1); gc.strokePolygon(xs,ys,v.size());
        }

        // Triangulation Delaunay (arêtes bleues)
        if (cbDelaunay.isSelected()) {
            gc.setStroke(Color.web("#4a90d9",0.4)); gc.setLineWidth(0.8); gc.setLineDashes(0);
            for (DelaunayTriangle t : triangles) {
                Coordinate[] v = t.getVertices();
                gc.strokeLine(sx(v[0]),sy(v[0]),sx(v[1]),sy(v[1]));
                gc.strokeLine(sx(v[1]),sy(v[1]),sx(v[2]),sy(v[2]));
                gc.strokeLine(sx(v[2]),sy(v[2]),sx(v[0]),sy(v[0]));
            }
        }

        // Arêtes Voronoï (pointillés rouges) + lignes d'assignation (pointillés violets)
        if (cbVoronoi.isSelected()) {
            gc.setStroke(Color.web("#e05c3e",0.75)); gc.setLineWidth(1.5); gc.setLineDashes(6,4);
            for (VoronoiEdge e : edges) gc.strokeLine(sx(e.getStart()),sy(e.getStart()),sx(e.getEnd()),sy(e.getEnd()));
            gc.setLineDashes(0);
        }
        gc.setStroke(Color.web("#7c3aed",0.5)); gc.setLineDashes(8,5);
        for (Map.Entry<Patient,Hospital> e : assignments.entrySet())
            gc.strokeLine(sx(e.getKey()),sy(e.getKey()),sx(e.getValue()),sy(e.getValue()));
        gc.setLineDashes(0);

        // Hôpitaux (cercles colorés selon saturation)
        for (Hospital h : hospitals) {
            if (h == selectedHospital) { gc.setStroke(Color.CYAN); gc.setLineWidth(3); gc.strokeOval(sx(h)-16,sy(h)-16,32,32); }
            gc.setFill(satColor(h.getSaturationRate())); gc.fillOval(sx(h)-9,sy(h)-9,18,18);
            gc.setFill(Color.BLACK); gc.setFont(Font.font(null,FontWeight.BOLD,11)); gc.fillText(h.getName(),sx(h)+12,sy(h)-1);
            gc.setFill(Color.DIMGRAY); gc.setFont(Font.font(10)); gc.fillText(String.format("%.0f%%",h.getSaturationRate()*100),sx(h)+12,sy(h)+11);
        }

        // Patients (petits cercles violets)
        gc.setFont(Font.font(null,FontWeight.BOLD,10));
        for (Patient p : patients) {
            gc.setFill(Color.web("#7c3aed")); gc.fillOval(sx(p)-5,sy(p)-5,10,10);
            gc.setFill(Color.web("#4c1d95")); gc.fillText(p.getName(),sx(p)+8,sy(p)+4);
        }
    }

    // --- Utilitaires ---

    // Transformations coordonnées monde → pixels écran (zoom + pan)
    private double sx(Coordinate c) { return panX + c.getLongitude() * zoom; }
    private double sy(Coordinate c) { return panY - c.getLatitude() * zoom; }
    private double sx(Hospital h)   { return sx(h.getPosition()); }
    private double sy(Hospital h)   { return sy(h.getPosition()); }
    private double sx(Patient p)    { return sx(p.getPosition()); }
    private double sy(Patient p)    { return sy(p.getPosition()); }
    private double screenToLat(double py) { return (panY - py) / zoom; }
    private double screenToLon(double px) { return (px - panX) / zoom; }
    private Color  satColor(double s)     { return Color.hsb(120*(1-Math.min(s,1)), 0.75, 0.72); }

    // Trie les sommets par angle polaire (requis par fillPolygon / strokePolygon)
    private List<Coordinate> sortedByAngle(List<Coordinate> v, Coordinate c) {
        List<Coordinate> r = new ArrayList<>(v);
        r.sort(Comparator.comparingDouble(p -> Math.atan2(p.getLongitude()-c.getLongitude(), p.getLatitude()-c.getLatitude())));
        return r;
    }

    private String zoneInfo(VoronoiZone z) {
        List<Patient> zp = new ArrayList<>();
        for (Patient p : patients) if (z.contains(p.getPosition())) zp.add(p);
        double surf = z.getSurface();
        double minD = 0, maxD = 0, avgD = 0;
        if (!zp.isEmpty()) {
            minD = Double.MAX_VALUE;
            for (Patient p : zp) {
                double d = p.getPosition().distanceTo(z.getHospital().getPosition());
                if (d < minD) minD = d;
                if (d > maxD) maxD = d;
                avgD += d;
            }
            avgD /= zp.size();
        }
        return String.format(
            "Zone : %s\nSurface : %.1f\nPatients : %d\nDensité : %.4f\nDist min : %.1f\nDist moy : %.1f\nDist max : %.1f\nSaturation : %.0f%%",
            z.getHospital().getName(), surf, zp.size(),
            surf > 0 ? zp.size() / surf : 0,
            minD, avgD, maxD, z.getHospital().getSaturationRate() * 100);
    }

    private DelaunayTriangle findTriangle(Coordinate p) {
        for (DelaunayTriangle t : triangles) if (inTriangle(p, t)) return t;
        return null;
    }

    private boolean inTriangle(Coordinate p, DelaunayTriangle t) {
        Coordinate[] v = t.getVertices();
        double d1 = cross(p,v[0],v[1]), d2 = cross(p,v[1],v[2]), d3 = cross(p,v[2],v[0]);
        return !((d1<0||d2<0||d3<0) && (d1>0||d2>0||d3>0));
    }

    private double cross(Coordinate p, Coordinate a, Coordinate b) {
        return (p.getLatitude()-b.getLatitude())*(a.getLongitude()-b.getLongitude())
             - (a.getLatitude()-b.getLatitude())*(p.getLongitude()-b.getLongitude());
    }

    private String triangleInfo(DelaunayTriangle t) {
        Coordinate[] v = t.getVertices();
        StringBuilder sb = new StringBuilder("Triangle\n");
        for (int i = 0; i < 3; i++) {
            final Coordinate vi = v[i];
            Hospital h = hospitals.stream().filter(x -> x.getPosition().equals(vi)).findFirst().orElse(null);
            long cnt = h == null ? 0 : assignments.entrySet().stream().filter(e -> e.getValue()==h).count();
            sb.append(String.format("  %s : %d patients\n", h!=null ? h.getName() : "?", cnt));
        }
        double surf = 0.5 * Math.abs(
            (v[1].getLatitude()-v[0].getLatitude())*(v[2].getLongitude()-v[0].getLongitude()) -
            (v[2].getLatitude()-v[0].getLatitude())*(v[1].getLongitude()-v[0].getLongitude()));
        sb.append(String.format("Surface : %.1f\nArêtes : %.1f / %.1f / %.1f",
            surf, v[0].distanceTo(v[1]), v[1].distanceTo(v[2]), v[2].distanceTo(v[0])));
        return sb.toString();
    }

    private String hospitalInfo(Hospital h) {
        return String.format("%s | %d/%d (%.0f%%)\n%s", h.getName(),
                h.getCurrentCapacity(), h.getMaxCapacity(), h.getSaturationRate()*100,
                h.getServices().stream().map(Enum::name).reduce((a,b) -> a+", "+b).orElse(""));
    }

    private HospitalServiceType parseService(String s) {
        try { return HospitalServiceType.valueOf(s.trim().toUpperCase()); }
        catch (Exception e) { return HospitalServiceType.GENERAL; }
    }

    private FileChooser picker(String title, String ext) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(ext, ext));
        return fc;
    }

    private void alert(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private Label bold(String t)   { Label l = new Label(t); l.setFont(Font.font(null,FontWeight.BOLD,13)); return l; }
    private Separator sep()        { return new Separator(); }

    private HBox row(Button... bs) {
        HBox box = new HBox(4, bs);
        for (Button b : bs) { HBox.setHgrow(b, Priority.ALWAYS); b.setMaxWidth(Double.MAX_VALUE); }
        return box;
    }

    private Button btn(String label, EventHandler<ActionEvent> action) {
        Button b = new Button(label); b.setOnAction(action); return b;
    }
}
