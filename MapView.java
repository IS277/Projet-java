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

    // Model state — centralized in MapManager
    private MapManager               map         = new MapManager();
    private final java.util.Map<Patient, Hospital> assignments = new LinkedHashMap<>();
    private List<VoronoiEdge>        edges       = new ArrayList<>();

    // Control state
    private Hospital selectedHospital;
    private boolean  addingHospital, placingPatient;
    private int      hid = 1, pid = 1;
    private Hospital dragged      = null;
    private boolean  wasDragged   = false;
    private double   zoom = SCALE, panX = MARGIN, panY = H - MARGIN;
    private boolean  panning      = false;
    private double   lastMX, lastMY;

    // Presentation
    private Stage    stage;
    private Canvas   canvas;
    private Button   btnAdd, btnRemove, btnPlace;
    private TextField hospName, patientName;
    private ComboBox<HospitalServiceType> svcCombo;
    private Spinner<Integer> randomCountSpinner;
    private Label    infoLabel, resultLabel;
    private CheckBox cbDelaunay      = new CheckBox("Delaunay");
    private CheckBox cbVoronoi       = new CheckBox("Voronoï");
    private CheckBox cbCircumcenters = new CheckBox("Circumcenters");
    private CheckBox cbGrid          = new CheckBox("Grille");
    private CheckBox cbLegend        = new CheckBox("Légende");
    private Label    statPatients    = new Label("Patients : 0");
    private Label    statSatAvg      = new Label("Sat. moy. : —");
    private Label    statSaturated   = new Label("Saturés : 0/0");
    private Hospital  hoveredHospital = null;
    private Patient   hoveredPatient  = null;
    private VoronoiZone hoveredZone   = null;
    private Patient   draggedPatient  = null;
    private Patient   selectedPatient = null;
    private Set<VoronoiZone> neighborZones  = new HashSet<>();
    private Label     statusBar       = new Label("lat: —  lon: —");

    public void show() {
        computeGeometry();

        canvas = new Canvas(W, H);
        cbDelaunay.setSelected(true); cbVoronoi.setSelected(true);
        cbDelaunay.setOnAction(e -> drawMap()); cbVoronoi.setOnAction(e -> drawMap());
        cbCircumcenters.setOnAction(e -> drawMap());
        cbGrid.setSelected(true);
        cbGrid.setOnAction(e -> drawMap());
        cbLegend.setSelected(true);
        cbLegend.setOnAction(e -> drawMap());
        canvas.setOnMouseMoved(e -> handleHover(e.getX(), e.getY()));

        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) { panning = true; }
            else {
                dragged = map.getHospitals().values().stream()
                    .filter(h -> Math.hypot(e.getX()-sx(h), e.getY()-sy(h)) < 14)
                    .findFirst().orElse(null);
                if (dragged == null) draggedPatient = findPatient(e.getX(), e.getY());
            }
            lastMX = e.getX(); lastMY = e.getY(); wasDragged = false;
        });
        canvas.setOnMouseDragged(e -> {
            try {
                wasDragged = true;
                if (panning) {
                    panX += e.getX() - lastMX; panY += e.getY() - lastMY; drawMap();
                } else if (dragged != null) {
                    map.moveHospital(dragged.getId(), new Coordinate(screenToLat(e.getY()), screenToLon(e.getX())));
                    refreshEdges(); drawMap();
                } else if (draggedPatient != null) {
                    map.movePatient(draggedPatient.getId(), new Coordinate(screenToLat(e.getY()), screenToLon(e.getX())));
                    Hospital best = AssignmentService.findBestHospital(
                            draggedPatient, new ArrayList<>(map.getHospitals().values()));
                    if (best != null) assignments.put(draggedPatient, best);
                    refreshEdges(); drawMap();
                }
                lastMX = e.getX(); lastMY = e.getY();
            } catch (Exception ex) { statusBar.setText("Error: " + ex.getMessage()); }
        });
        canvas.setOnMouseReleased(e -> { dragged = null; draggedPatient = null; panning = false; });
        canvas.setOnMouseClicked(e -> { if (!wasDragged) handleClick(e.getX(), e.getY(), e.getButton()); });
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
        randomCountSpinner = new Spinner<>(1, 500, 10);
        randomCountSpinner.setEditable(true);
        randomCountSpinner.setPrefWidth(75);
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
            map.removeHospital(selectedHospital.getId());
            selectedHospital = null;
            refreshEdges(); btnRemove.setDisable(true);
            infoLabel.setText("Click a hospital."); drawMap();
        });
        btnPlace = new Button("Place Patient");
        btnPlace.setOnAction(e -> {
            placingPatient = true; addingHospital = false;
            btnPlace.setText("Click map…"); canvas.setCursor(Cursor.CROSSHAIR);
        });

        VBox panel = new VBox(10,
            bold("View"), new HBox(8, cbDelaunay, cbVoronoi),
                          new HBox(8, cbGrid, cbCircumcenters, cbLegend),
            sep(), bold("File"),    row(btn("Import CSV", e -> importCSV()),
                                        btn("Export CSV", e -> exportCSV())),
                                    row(btn("Save",       e -> saveMap()),
                                        btn("Load",       e -> loadMap())),
            sep(), bold("Hospital"), hospName, row(btnAdd, btnRemove), infoLabel,
            sep(), bold("Patient"),  patientName, svcCombo,
                                     row(btnPlace,
                                         btn("Clear", e -> {
                                             map.clearPatients();
                                             assignments.clear();
                                             selectedPatient = null;
                                             neighborZones.clear();
                                             resultLabel.setText("—");
                                             refreshEdges();
                                             drawMap();
                                         })),
                                     new HBox(6, new Label("N :"), randomCountSpinner,
                                         btn("Add Random", e -> addRandomPatientsBulk(
                                                 randomCountSpinner.getValue()))),
            sep(), bold("Statistiques"), statPatients, statSatAvg, statSaturated,
            sep(), bold("Result"), resultLabel,
            sep(), btn("Simulation", e -> {
                       SimulationView sv = new SimulationView(new ArrayList<>(map.getHospitals().values()));
                       sv.setOnPatientPlaced(p -> {
                           map.addPatient(p);
                           Hospital best = AssignmentService.findBestHospital(
                                   p, new ArrayList<>(map.getHospitals().values()));
                           if (best != null) { assignments.put(p, best); best.admitPatient(); }
                           refreshEdges(); drawMap();
                       });
                       sv.show();
                   }),
                   btn("Reset view", e -> { zoom = SCALE; panX = MARGIN; panY = H - MARGIN; drawMap(); }));
        panel.setPadding(new Insets(16)); panel.setPrefWidth(260);

        statusBar.setStyle("-fx-padding: 2 8; -fx-font-size: 10px; -fx-text-fill: #666;");

        BorderPane root = new BorderPane();
        root.setLeft(canvas); root.setRight(panel);
        root.setBottom(statusBar);

        stage = new Stage();
        stage.setTitle("Emergency Dispatcher");
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
        drawMap();
    }

    // Canvas click handler — 3 modes: add hospital / place patient / select
    private void handleClick(double mx, double my, MouseButton button) {
        try {
        if (addingHospital) {
            String name = hospName.getText().isBlank() ? "H" + hid : hospName.getText();
            Hospital h  = new Hospital("H" + hid++, name,
                    new Coordinate(screenToLat(my), screenToLon(mx)), 100);
            h.addService(HospitalServiceType.GENERAL);
            map.addHospital(h); refreshEdges();
            addingHospital = false; btnAdd.setText("Add Hospital"); canvas.setCursor(Cursor.DEFAULT);
        } else if (placingPatient) {
            placePatient(patientName.getText(), screenToLat(my), screenToLon(mx));
            placingPatient = false; btnPlace.setText("Place Patient"); canvas.setCursor(Cursor.DEFAULT);
        } else {
            Patient clickedP = findPatient(mx, my);
            if (clickedP != null) {
                if (button == MouseButton.SECONDARY) {
                    // Right-click → remove patient
                    map.removePatient(clickedP.getId());
                    assignments.remove(clickedP);
                    if (clickedP == selectedPatient) { selectedPatient = null; neighborZones = new HashSet<>(); }
                    refreshEdges(); drawMap(); return;
                } else {
                    // Left-click → toggle selection, highlight assigned zone + neighbors
                    selectedPatient = (selectedPatient == clickedP) ? null : clickedP;
                    if (selectedPatient != null) {
                        Hospital h = assignments.get(selectedPatient);
                        neighborZones = h != null ? getNeighborZones(h) : new HashSet<>();
                        infoLabel.setText("Patient : " + selectedPatient.getName()
                            + "\nService : " + selectedPatient.getRequiredService().name()
                            + "\nHôpital : " + (h != null ? h.getName() : "—")
                            + "\nZones voisines : " + neighborZones.size());
                    } else {
                        neighborZones = new HashSet<>();
                        infoLabel.setText("Click a hospital.");
                    }
                    drawMap(); return;
                }
            }
            selectedHospital = map.getHospitals().values().stream()
                    .filter(h -> Math.hypot(mx - sx(h), my - sy(h)) < 14)
                    .findFirst().orElse(null);
            selectedPatient = null;
            btnRemove.setDisable(selectedHospital == null);
            if (selectedHospital != null) {
                infoLabel.setText(hospitalInfo(selectedHospital));
                List<Patient> assigned = new ArrayList<>();
                assignments.forEach((p, h) -> { if (h == selectedHospital) assigned.add(p); });
                VoronoiZone zone = map.getZones().stream()
                        .filter(z -> z.getHospital() == selectedHospital).findFirst().orElse(null);
                new HospitalDetailView().show(selectedHospital, assigned, zone);
            } else {
                Coordinate wp = new Coordinate(screenToLat(my), screenToLon(mx));
                VoronoiZone z = map.getZones().stream()
                        .filter(zn -> zn.contains(wp)).findFirst().orElse(null);
                DelaunayTriangle t = findTriangle(wp);
                if (z != null) {
                    infoLabel.setText("Zone : " + z.getHospital().getName()
                        + "\nPatients : " + z.getPatientCount()
                        + "\nSurface : " + String.format("%.1f", z.getSurface())
                        + "\nSaturation : " + String.format("%.0f%%", z.getHospital().getSaturationRate() * 100));
                    new ZoneDetailView().show(z, map.getZones());
                } else if (t != null) {
                    infoLabel.setText(triangleInfo(t));
                    new TriangleDetailView().show(t, assignments);
                } else {
                    infoLabel.setText("Click a hospital or zone.");
                }
            }
        }
        drawMap();
        } catch (Exception ex) { infoLabel.setText("Error: " + ex.getMessage()); }
    }

    // --- Model ---

    // Delegates recomputation to MapManager, then updates Voronoi edges (not managed by MapManager).
    private void computeGeometry() {
        if (map.getHospitals().size() < 3) { edges = new ArrayList<>(); return; }
        map.recompute();
        refreshEdges();
    }

    // Recomputes only the Voronoi edges after a MapManager API call (which already ran recompute).
    private void refreshEdges() {
        try {
            edges = map.getHospitals().size() >= 3
                    ? new VoronoiService().getVoronoiEdges(map.getTriangles())
                    : new ArrayList<>();
        } catch (Exception ex) { edges = new ArrayList<>(); }
    }

    private void placePatient(String rawName, double lat, double lon) {
        int id = pid++;
        Patient p = new Patient("P"+id, rawName.isBlank() ? "P"+id : rawName,
                new Coordinate(lat, lon), svcCombo.getValue());
        map.addPatient(p);
        Hospital best = AssignmentService.findBestHospital(
                p, new ArrayList<>(map.getHospitals().values()));
        if (best != null) {
            assignments.put(p, best);
            best.admitPatient();
            resultLabel.setText(String.format("%s%n%.1f units — %.0f%% full",
                    best.getName(), p.getPosition().distanceTo(best.getPosition()),
                    best.getSaturationRate() * 100));
            resultLabel.setStyle("-fx-text-fill:#198754;");
        } else {
            resultLabel.setText("No hospital for this service.");
            resultLabel.setStyle("-fx-text-fill:#dc3545;");
        }
        refreshEdges(); drawMap();
    }

    // --- Control: file operations ---

    private void importCSV() {
        File f = picker("Import Hospitals","*.csv").showOpenDialog(stage);
        if (f == null) return;
        int n = 0;
        List<String> errors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            int lineNum = 0;
            for (String raw; (raw = br.readLine()) != null; ) {
                lineNum++;
                // strip BOM if present on first line
                String line = (lineNum == 1 && raw.startsWith("﻿")) ? raw.substring(1) : raw;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(",");
                try {
                    String id, name;
                    double lat, lon;
                    int cap;
                    HospitalServiceType svc;
                    // Auto-detect format by checking if p[1] is numeric:
                    //   p[1] numeric  → Name,lat,lon,cap[,svc]
                    //   p[1] not num  → ID,Name,lat,lon,cap[,svc]
                    if (p.length >= 4 && isNumeric(p[1].trim())) {
                        // Name,lat,lon,cap[,svc]
                        id   = "H" + hid;
                        name = p[0].trim();
                        lat  = Double.parseDouble(p[1].trim());
                        lon  = Double.parseDouble(p[2].trim());
                        cap  = Integer.parseInt(p[3].trim());
                        svc  = p.length >= 5 ? parseService(p[4]) : HospitalServiceType.GENERAL;
                    } else if (p.length >= 5 && isNumeric(p[2].trim())) {
                        // ID,Name,lat,lon,cap[,svc]
                        id   = p[0].trim();
                        name = p[1].trim();
                        lat  = Double.parseDouble(p[2].trim());
                        lon  = Double.parseDouble(p[3].trim());
                        cap  = Integer.parseInt(p[4].trim());
                        svc  = p.length >= 6 ? parseService(p[5]) : HospitalServiceType.GENERAL;
                    } else {
                        errors.add("Ligne " + lineNum + " ignorée (trop peu de colonnes) : " + line);
                        continue;
                    }
                    Hospital h = new Hospital(id, name, new Coordinate(lat, lon), cap);
                    h.addService(svc);
                    map.addHospital(h);
                    hid++; n++;
                } catch (Exception ex) {
                    errors.add("Ligne " + lineNum + " : " + ex.getMessage());
                }
            }
        } catch (IOException ex) { alert(ex.getMessage()); return; }
        refreshEdges(); drawMap();
        String msg = n + " hôpital(s) importé(s).";
        if (!errors.isEmpty()) msg += "\n\nLignes ignorées :\n" + String.join("\n", errors);
        alert(msg);
    }

    private void exportCSV() {
        if (map.getHospitals().isEmpty()) { alert("No hospitals to export."); return; }
        File f = picker("Export Hospitals", "*.csv").showSaveDialog(stage);
        if (f == null) return;
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"))) {
            pw.println("# id,name,lat,lon,capacity,service");
            for (Hospital h : map.getHospitals().values()) {
                String svc = h.getServices().isEmpty() ? "GENERAL"
                        : h.getServices().iterator().next().name();
                pw.printf("%s,%s,%.6f,%.6f,%d,%s%n",
                        h.getId(), h.getName(),
                        h.getPosition().getLatitude(), h.getPosition().getLongitude(),
                        h.getMaxCapacity(), svc);
            }
            alert(map.getHospitals().size() + " hospital(s) exported.");
        } catch (IOException ex) { alert("Error: " + ex.getMessage()); }
    }

    // Save via MapPersistenceService (serializes the entire MapManager)
    private void saveMap() {
        File f = picker("Save Map","*.map").showSaveDialog(stage);
        if (f == null) return;
        try {
            new MapPersistenceService().saveMap(map, f.getAbsolutePath());
            alert("Map saved.");
        } catch (IOException ex) { alert("Error: " + ex.getMessage()); }
    }

    // Load via MapPersistenceService
    private void loadMap() {
        File f = picker("Load Map","*.map").showOpenDialog(stage);
        if (f == null) return;
        try {
            map = new MapPersistenceService().loadMap(f.getAbsolutePath());
            map.initializeAfterLoading();
            assignments.clear(); selectedHospital = null; selectedPatient = null;
            edges = new VoronoiService().getVoronoiEdges(map.getTriangles());
            drawMap(); alert("Map loaded.");
        } catch (Exception ex) { alert("Error: " + ex.getMessage()); }
    }

    private void addRandomPatientsBulk(int n) {
        if (map.getHospitals().isEmpty()) { alert("Add at least one hospital first."); return; }
        try {
        Random rng = new Random();
        double latMin = screenToLat(H - MARGIN), latMax = screenToLat(MARGIN);
        double lonMin = screenToLon(MARGIN),     lonMax = screenToLon(W - MARGIN);
        if (latMax <= latMin || lonMax <= lonMin) {
            latMin = 5; latMax = 155; lonMin = 5; lonMax = 235;
        }
        HospitalServiceType[] services = HospitalServiceType.values();
        for (int i = 0; i < n; i++) {
            int id = pid++;
            Patient p = new Patient("P" + id, "P" + id,
                    new Coordinate(latMin + rng.nextDouble() * (latMax - latMin),
                                   lonMin + rng.nextDouble() * (lonMax - lonMin)),
                    services[rng.nextInt(services.length)]);
            map.addPatient(p);
        }
        
        for (Patient p : map.getPatients().values())
            if (!assignments.containsKey(p)) {
                Hospital best = AssignmentService.findBestHospital(
                        p, new ArrayList<>(map.getHospitals().values()));
                if (best != null) assignments.put(p, best);
            }
        refreshEdges();
        drawMap();
        } catch (Exception ex) { alert("Error adding patients: " + ex.getMessage()); }
    }

    // --- Presentation: Canvas rendering ---

    private void drawMap() {
        try {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, W, H);
        gc.setFill(Color.web("#e8f4f8")); gc.fillRect(0, 0, W, H);
        if (cbGrid.isSelected()) drawGrid(gc);

        // Voronoi zones (fillPolygon requires vertices sorted by angle)
        for (VoronoiZone z : map.getZones()) {
            List<Coordinate> v = sortedByAngle(z.getVertices(), z.getCenter());
            if (v.size() < 3) continue;
            double[] xs = v.stream().mapToDouble(this::sx).toArray();
            double[] ys = v.stream().mapToDouble(this::sy).toArray();
            Color c = satColor(z.getHospital().getSaturationRate());
            boolean isSelZone  = selectedPatient != null && assignments.get(selectedPatient) == z.getHospital();
            boolean isNeighbor = !isSelZone && neighborZones.contains(z);
            boolean isHovZone  = z == hoveredZone;
            double alpha = isSelZone ? 0.45 : isNeighbor ? 0.35 : isHovZone ? 0.35 : 0.18;
            Color fill = isNeighbor ? Color.web("#fb923c", alpha) : c.deriveColor(0,1,1,alpha);
            gc.setFill(fill); gc.fillPolygon(xs,ys,v.size());
            gc.setStroke(isSelZone  ? Color.web("#f59e0b",0.9)
                       : isNeighbor ? Color.web("#ea580c",0.85)
                       : c.deriveColor(0,1,0.8,0.5));
            gc.setLineWidth(isSelZone || isNeighbor ? 2.5 : 1); gc.strokePolygon(xs,ys,v.size());
        }

        // Delaunay triangulation (blue edges)
        if (cbDelaunay.isSelected()) {
            gc.setStroke(Color.web("#4a90d9",0.4)); gc.setLineWidth(0.8); gc.setLineDashes(0);
            for (DelaunayTriangle t : map.getTriangles()) {
                Coordinate[] v = t.getVertices();
                gc.strokeLine(sx(v[0]),sy(v[0]),sx(v[1]),sy(v[1]));
                gc.strokeLine(sx(v[1]),sy(v[1]),sx(v[2]),sy(v[2]));
                gc.strokeLine(sx(v[2]),sy(v[2]),sx(v[0]),sy(v[0]));
            }
        }

        // Circumcenters (small blue dots at the center of each circumscribed circle)
        if (cbCircumcenters.isSelected()) {
            gc.setFill(Color.web("#1a56db", 0.85));
            for (DelaunayTriangle t : map.getTriangles()) {
                Coordinate cc = t.getCircumcenter();
                gc.fillOval(sx(cc) - 3.5, sy(cc) - 3.5, 7, 7);
            }
        }

        // Voronoi edges (red dashes) + patient assignment lines (purple dashes)
        if (cbVoronoi.isSelected()) {
            gc.setStroke(Color.web("#e05c3e",0.75)); gc.setLineWidth(1.5); gc.setLineDashes(6,4);
            for (VoronoiEdge e : edges)
                gc.strokeLine(sx(e.getStart()),sy(e.getStart()),sx(e.getEnd()),sy(e.getEnd()));
            gc.setLineDashes(0);
        }
        gc.setStroke(Color.web("#7c3aed",0.5)); gc.setLineDashes(8,5);
        for (java.util.Map.Entry<Patient,Hospital> e : assignments.entrySet())
            gc.strokeLine(sx(e.getKey()),sy(e.getKey()),sx(e.getValue()),sy(e.getValue()));
        gc.setLineDashes(0);

        // Hospitals (color-coded circles by saturation rate)
        for (Hospital h : map.getHospitals().values()) {
            if (h == hoveredHospital)  { gc.setStroke(Color.web("#60a5fa",0.8)); gc.setLineWidth(2);  gc.strokeOval(sx(h)-13,sy(h)-13,26,26); }
            if (h == selectedHospital) { gc.setStroke(Color.CYAN);               gc.setLineWidth(3);  gc.strokeOval(sx(h)-16,sy(h)-16,32,32); }
            gc.setFill(satColor(h.getSaturationRate())); gc.fillOval(sx(h)-9,sy(h)-9,18,18);
            gc.setFill(Color.BLACK);   gc.setFont(Font.font(null,FontWeight.BOLD,11)); gc.fillText(h.getName(),sx(h)+12,sy(h)-1);
            gc.setFill(Color.DIMGRAY); gc.setFont(Font.font(10)); gc.fillText(String.format("%.0f%%",h.getSaturationRate()*100),sx(h)+12,sy(h)+11);
        }

        // Patients (small circles — purple/yellow/lavender depending on state)
        gc.setFont(Font.font(null,FontWeight.BOLD,10));
        for (Patient p : map.getPatients().values()) {
            Color pc = (p == selectedPatient) ? Color.web("#f59e0b")
                     : (p == hoveredPatient)  ? Color.web("#a78bfa")
                     : Color.web("#7c3aed");
            gc.setFill(pc); gc.fillOval(sx(p)-5,sy(p)-5,10,10);
            if (p == selectedPatient) { gc.setStroke(Color.web("#d97706",0.9)); gc.setLineWidth(2); gc.strokeOval(sx(p)-7,sy(p)-7,14,14); }
            gc.setFill(Color.web("#4c1d95")); gc.fillText(p.getName(),sx(p)+8,sy(p)+4);
        }

        if (cbLegend.isSelected()) drawLegend(gc);
        updateStats();
        } catch (Exception ex) { statusBar.setText("Draw error: " + ex.getMessage()); }
    }

    // --- Utilities ---

    private double sx(Coordinate c) { return panX + c.getLongitude() * zoom; }
    private double sy(Coordinate c) { return panY - c.getLatitude() * zoom; }
    private double sx(Hospital h)   { return sx(h.getPosition()); }
    private double sy(Hospital h)   { return sy(h.getPosition()); }
    private double sx(Patient p)    { return sx(p.getPosition()); }
    private double sy(Patient p)    { return sy(p.getPosition()); }
    private double screenToLat(double py) { return (panY - py) / zoom; }
    private double screenToLon(double px) { return (px - panX) / zoom; }
    private Color  satColor(double s)     { return Color.hsb(120*(1-Math.min(s,1)), 0.75, 0.72); }

    private List<Coordinate> sortedByAngle(List<Coordinate> v, Coordinate c) {
        List<Coordinate> r = new ArrayList<>(v);
        r.sort(Comparator.comparingDouble(p -> Math.atan2(
                p.getLongitude()-c.getLongitude(), p.getLatitude()-c.getLatitude())));
        return r;
    }

    private String zoneInfo(VoronoiZone z) {
        return String.format(
            "Zone : %s\nSurface : %.1f\nPatients : %d\nDensité : %.4f\nDist min : %.1f\nDist moy : %.1f\nDist max : %.1f\nSaturation : %.0f%%",
            z.getHospital().getName(),
            z.getSurface(),
            z.getPatientCount(),
            z.getDensity(),
            z.getMinDistanceToHospital(),
            z.getAverageDistanceToHospital(),
            z.getMaxDistanceToHospital(),
            z.getHospital().getSaturationRate() * 100);
    }

    private DelaunayTriangle findTriangle(Coordinate p) {
        for (DelaunayTriangle t : map.getTriangles()) if (inTriangle(p, t)) return t;
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
            Hospital h = map.getHospitals().values().stream()
                    .filter(x -> x.getPosition().equals(vi)).findFirst().orElse(null);
            long cnt = h == null ? 0 : assignments.entrySet().stream()
                    .filter(e -> e.getValue() == h).count();
            sb.append(String.format("  %s : %d patients\n", h != null ? h.getName() : "?", cnt));
        }
        sb.append(String.format("Surface : %.1f\nArêtes : %.1f / %.1f / %.1f",
            t.getSurface(), v[0].distanceTo(v[1]), v[1].distanceTo(v[2]), v[2].distanceTo(v[0])));
        return sb.toString();
    }

    private String hospitalInfo(Hospital h) {
        return String.format("%s | %d/%d (%.0f%%)\n%s", h.getName(),
                h.getCurrentCapacity(), h.getMaxCapacity(), h.getSaturationRate()*100,
                h.getServices().stream().map(Enum::name).reduce((a,b) -> a+", "+b).orElse(""));
    }

    private boolean isNumeric(String s) {
        try { Double.parseDouble(s); return true; }
        catch (NumberFormatException e) { return false; }
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

    // Updates global statistics labels in the side panel
    private void updateStats() {
        int np = map.getPatients().size();
        double avgSat = map.getHospitals().values().stream()
                .mapToDouble(Hospital::getSaturationRate).average().orElse(0);
        long sat = map.getHospitals().values().stream()
                .filter(h -> h.getSaturationRate() >= 0.8).count();
        statPatients.setText("Patients : " + np);
        statSatAvg.setText(String.format("Sat. moy. : %.0f%%", avgSat * 100));
        statSaturated.setText("Saturés : " + sat + " / " + map.getHospitals().size());
        statSaturated.setTextFill(sat > 0 ? Color.RED : Color.DARKGREEN);
    }

    // Fixed legend overlay in the top-right corner of the canvas
    private void drawLegend(GraphicsContext gc) {
        double x = W - 162, y = 10;
        gc.setFill(Color.web("#ffffffd0")); gc.fillRoundRect(x - 6, y - 6, 158, 148, 8, 8);
        gc.setStroke(Color.web("#cccccc")); gc.setLineWidth(0.8); gc.strokeRoundRect(x - 6, y - 6, 158, 148, 8, 8);

        gc.setFont(Font.font(null, FontWeight.BOLD, 10));
        gc.setFill(Color.DARKGRAY); gc.fillText("Legend", x, y + 9); y += 18;

        // Hospital saturation levels
        for (double[] e : new double[][]{{0.2, 0}, {0.55, 0}, {0.9, 0}}) {
            gc.setFill(satColor(e[0])); gc.fillOval(x + 1, y + 1, 10, 10);
            gc.setFont(Font.font(9)); gc.setFill(Color.DARKGRAY);
            String lbl = e[0] < 0.4 ? "Hospital — low saturation"
                       : e[0] < 0.7 ? "Hospital — medium saturation"
                       :               "Hospital — high saturation (>80%)";
            gc.fillText(lbl, x + 15, y + 9); y += 16;
        }

        // Patient dot
        gc.setFill(Color.web("#7c3aed")); gc.fillOval(x + 2, y + 2, 8, 8);
        gc.setFont(Font.font(9)); gc.setFill(Color.DARKGRAY);
        gc.fillText("Patient", x + 15, y + 9); y += 16;

        // Assignment line
        gc.setStroke(Color.web("#7c3aed", 0.6)); gc.setLineWidth(1.5); gc.setLineDashes(5, 3);
        gc.strokeLine(x + 1, y + 5, x + 12, y + 5); gc.setLineDashes(0);
        gc.setFont(Font.font(9)); gc.setFill(Color.DARKGRAY);
        gc.fillText("Patient assignment", x + 15, y + 9); y += 16;

        // Voronoi boundary
        gc.setStroke(Color.web("#e05c3e", 0.8)); gc.setLineWidth(1.5); gc.setLineDashes(4, 3);
        gc.strokeLine(x + 1, y + 5, x + 12, y + 5); gc.setLineDashes(0);
        gc.setFont(Font.font(9)); gc.setFill(Color.DARKGRAY);
        gc.fillText("Voronoi boundary", x + 15, y + 9); y += 16;

        // Delaunay edge
        gc.setStroke(Color.web("#4a90d9", 0.6)); gc.setLineWidth(1);
        gc.strokeLine(x + 1, y + 5, x + 12, y + 5);
        gc.setFont(Font.font(9)); gc.setFill(Color.DARKGRAY);
        gc.fillText("Delaunay edge", x + 15, y + 9);
    }

    // Adaptive grid: world-coordinate step, ~8 lines visible at any zoom level
    private void drawGrid(GraphicsContext gc) {
        double worldW = W / zoom;
        double rawStep = worldW / 8.0;
        double exp = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double step = Math.ceil(rawStep / exp) * exp;
        if (step < 1) step = 1;

        double lonMin = screenToLon(0), lonMax = screenToLon(W);
        double latMin = screenToLat(H), latMax = screenToLat(0);

        gc.setStroke(Color.web("#b0c4d8", 0.55)); gc.setLineWidth(0.5); gc.setLineDashes(0);
        double startLon = Math.floor(lonMin / step) * step;
        for (double lon = startLon; lon <= lonMax + step; lon += step) {
            double x = panX + lon * zoom;
            gc.strokeLine(x, 0, x, H);
        }
        double startLat = Math.floor(latMin / step) * step;
        for (double lat = startLat; lat <= latMax + step; lat += step) {
            double y = panY - lat * zoom;
            gc.strokeLine(0, y, W, y);
        }

        gc.setFill(Color.web("#7a90a8", 0.75)); gc.setFont(Font.font(9));
        for (double lon = startLon; lon <= lonMax + step; lon += step) {
            double x = panX + lon * zoom;
            if (x > 4 && x < W - 20) gc.fillText(String.format("%.0f", lon), x + 2, H - 4);
        }
        for (double lat = startLat; lat <= latMax + step; lat += step) {
            double y = panY - lat * zoom;
            if (y > 10 && y < H - 4) gc.fillText(String.format("%.0f", lat), 2, y - 2);
        }
    }

    // Updates hover state, cursor and status bar; redraws only when the hovered element changes
    private void handleHover(double mx, double my) {
        Hospital newHH = map.getHospitals().values().stream()
                .filter(h -> Math.hypot(mx - sx(h), my - sy(h)) < 16)
                .findFirst().orElse(null);
        Patient newHP = findPatient(mx, my);
        Coordinate wp = new Coordinate(screenToLat(my), screenToLon(mx));
        VoronoiZone newHZ = (newHH == null && newHP == null)
                ? map.getZones().stream().filter(z -> z.contains(wp)).findFirst().orElse(null)
                : null;
        canvas.setCursor((newHH != null || newHP != null) ? Cursor.HAND : Cursor.DEFAULT);
        statusBar.setText(String.format("lat: %.1f  lon: %.1f  |  zoom: %.2f×",
                screenToLat(my), screenToLon(mx), zoom));
        if (newHH != hoveredHospital || newHP != hoveredPatient || newHZ != hoveredZone) {
            hoveredHospital = newHH; hoveredPatient = newHP; hoveredZone = newHZ;
            drawMap();
        }
    }

    // Returns the first patient within 8px of the cursor position
    private Patient findPatient(double mx, double my) {
        for (Patient p : map.getPatients().values())
            if (Math.hypot(mx - sx(p), my - sy(p)) <= 8) return p;
        return null;
    }

    // Returns the Voronoi zones adjacent to hospital h (sharing a Delaunay edge)
    private Set<VoronoiZone> getNeighborZones(Hospital h) {
        Set<Hospital> neighbors = new HashSet<>();
        for (DelaunayTriangle t : map.getTriangles()) {
            Coordinate[] v = t.getVertices();
            boolean hasH = false;
            for (Coordinate c : v) if (c.equals(h.getPosition())) { hasH = true; break; }
            if (!hasH) continue;
            for (Coordinate c : v) {
                if (!c.equals(h.getPosition())) {
                    map.getHospitals().values().stream()
                        .filter(o -> o.getPosition().equals(c))
                        .findFirst().ifPresent(neighbors::add);
                }
            }
        }
        Set<VoronoiZone> result = new HashSet<>();
        for (VoronoiZone z : map.getZones())
            if (neighbors.contains(z.getHospital())) result.add(z);
        return result;
    }
}