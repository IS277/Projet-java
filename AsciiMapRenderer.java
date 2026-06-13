import java.util.List;

/**
 * Classe utilitaire responsable de l'affichage ASCII des cartes.
 *
 * Cette classe appartient à la couche d'interface console. Elle ne modifie pas
 * le modèle métier : elle reçoit des hôpitaux, des patients, des triangles ou
 * des arêtes de Voronoï, puis les affiche dans une grille de caractères.
 *
 * Toutes les méthodes sont statiques car la classe ne conserve aucun état.
 *
 * @author Équipe Projet Emergency Dispatcher
 * @version 1.0
 */
public class AsciiMapRenderer {

    /**
     * Constructeur privé pour empêcher l'instanciation de cette classe utilitaire.
     */
    private AsciiMapRenderer() {
    }

    /**
     * Affiche les hôpitaux et la triangulation de Delaunay dans la console.
     *
     * @param hospitals liste des hôpitaux à afficher
     * @param triangles triangles de Delaunay à dessiner
     * @param width largeur de la grille ASCII
     * @param height hauteur de la grille ASCII
     */
    public static void drawHospitalsAndDelaunay(
            List<Hospital> hospitals,
            List<DelaunayTriangle> triangles,
            int width,
            int height) {

        char[][] grid = createGrid(width, height);

        double maxX = 0;
        double maxY = 0;

        for (Hospital hospital : hospitals) {
            maxX = Math.max(maxX, hospital.getPosition().getLatitude());
            maxY = Math.max(maxY, hospital.getPosition().getLongitude());
        }

        // Les arêtes des triangles sont dessinées avant les hôpitaux
        // pour que les hôpitaux restent visibles par-dessus.
        for (DelaunayTriangle triangle : triangles) {
            Coordinate[] vertices = triangle.getVertices();

            drawLine(grid, vertices[0], vertices[1], maxX, maxY, width, height);
            drawLine(grid, vertices[1], vertices[2], maxX, maxY, width, height);
            drawLine(grid, vertices[2], vertices[0], maxX, maxY, width, height);
        }

        drawHospitals(grid, hospitals, maxX, maxY, width, height);
        printGrid(grid);
        printHospitalLegend(hospitals);
    }

    /**
     * Affiche les hôpitaux, les patients et la triangulation de Delaunay.
     *
     * @param hospitals liste des hôpitaux à afficher
     * @param patients liste des patients à afficher
     * @param triangles triangles de Delaunay à dessiner
     * @param width largeur de la grille ASCII
     * @param height hauteur de la grille ASCII
     */
    public static void drawHospitalsPatientsAndDelaunay(
            List<Hospital> hospitals,
            List<Patient> patients,
            List<DelaunayTriangle> triangles,
            int width,
            int height) {

        char[][] grid = createGrid(width, height);

        double maxX = 0;
        double maxY = 0;

        for (Hospital hospital : hospitals) {
            maxX = Math.max(maxX, hospital.getPosition().getLatitude());
            maxY = Math.max(maxY, hospital.getPosition().getLongitude());
        }

        for (Patient patient : patients) {
            maxX = Math.max(maxX, patient.getPosition().getLatitude());
            maxY = Math.max(maxY, patient.getPosition().getLongitude());
        }

        for (DelaunayTriangle triangle : triangles) {
            Coordinate[] vertices = triangle.getVertices();

            drawLine(grid, vertices[0], vertices[1], maxX, maxY, width, height);
            drawLine(grid, vertices[1], vertices[2], maxX, maxY, width, height);
            drawLine(grid, vertices[2], vertices[0], maxX, maxY, width, height);
        }

        drawPatients(grid, patients, maxX, maxY, width, height);
        drawHospitals(grid, hospitals, maxX, maxY, width, height);

        printGrid(grid);

        System.out.println();
        System.out.println("Legend:");
        System.out.println("* = Delaunay edge");
        System.out.println("P = patient");

        printHospitalLegend(hospitals);
    }

    /**
     * Affiche les hôpitaux et les arêtes du diagramme de Voronoï.
     *
     * @param hospitals liste des hôpitaux à afficher
     * @param edges arêtes de Voronoï à dessiner
     * @param width largeur de la grille ASCII
     * @param height hauteur de la grille ASCII
     */
    public static void drawHospitalsAndVoronoi(
            List<Hospital> hospitals,
            List<VoronoiEdge> edges,
            int width,
            int height) {

        char[][] grid = createGrid(width, height);

        double maxX = 0;
        double maxY = 0;

        for (Hospital hospital : hospitals) {
            maxX = Math.max(maxX, hospital.getPosition().getLatitude());
            maxY = Math.max(maxY, hospital.getPosition().getLongitude());
        }

        for (VoronoiEdge edge : edges) {
            maxX = Math.max(maxX, edge.getStart().getLatitude());
            maxX = Math.max(maxX, edge.getEnd().getLatitude());

            maxY = Math.max(maxY, edge.getStart().getLongitude());
            maxY = Math.max(maxY, edge.getEnd().getLongitude());
        }

        for (VoronoiEdge edge : edges) {
            drawLine(grid, edge.getStart(), edge.getEnd(), maxX, maxY, width, height);
        }

        drawHospitals(grid, hospitals, maxX, maxY, width, height);

        printGrid(grid);

        System.out.println();
        System.out.println("Legend:");
        System.out.println("* = Voronoi edge");

        printHospitalLegend(hospitals);
    }

    /**
     * Affiche les hôpitaux, les patients et les arêtes de Voronoï.
     *
     * @param hospitals liste des hôpitaux à afficher
     * @param patients liste des patients à afficher
     * @param edges arêtes de Voronoï à dessiner
     * @param width largeur de la grille ASCII
     * @param height hauteur de la grille ASCII
     */
    public static void drawHospitalsPatientsAndVoronoi(
            List<Hospital> hospitals,
            List<Patient> patients,
            List<VoronoiEdge> edges,
            int width,
            int height) {

        char[][] grid = createGrid(width, height);

        double maxX = 0;
        double maxY = 0;

        for (Hospital hospital : hospitals) {
            maxX = Math.max(maxX, hospital.getPosition().getLatitude());
            maxY = Math.max(maxY, hospital.getPosition().getLongitude());
        }

        for (Patient patient : patients) {
            maxX = Math.max(maxX, patient.getPosition().getLatitude());
            maxY = Math.max(maxY, patient.getPosition().getLongitude());
        }

        for (VoronoiEdge edge : edges) {
            maxX = Math.max(maxX, edge.getStart().getLatitude());
            maxX = Math.max(maxX, edge.getEnd().getLatitude());

            maxY = Math.max(maxY, edge.getStart().getLongitude());
            maxY = Math.max(maxY, edge.getEnd().getLongitude());
        }

        for (VoronoiEdge edge : edges) {
            drawLine(grid, edge.getStart(), edge.getEnd(), maxX, maxY, width, height);
        }

        drawPatients(grid, patients, maxX, maxY, width, height);
        drawHospitals(grid, hospitals, maxX, maxY, width, height);

        printGrid(grid);

        System.out.println();
        System.out.println("Legend:");
        System.out.println("* = Voronoi edge");
        System.out.println("P = patient");

        printHospitalLegend(hospitals);
    }

    /**
     * Crée une grille ASCII initialisée avec des points.
     *
     * @param width largeur de la grille
     * @param height hauteur de la grille
     * @return grille initialisée
     */
    private static char[][] createGrid(int width, int height) {
        char[][] grid = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }

        return grid;
    }

    /**
     * Dessine une ligne entre deux coordonnées sur la grille.
     *
     * La méthode est utilisée pour représenter les arêtes de Delaunay
     * et les arêtes de Voronoï.
     *
     * @param grid grille ASCII
     * @param a première coordonnée
     * @param b seconde coordonnée
     * @param maxX valeur maximale en abscisse
     * @param maxY valeur maximale en ordonnée
     * @param width largeur de la grille
     * @param height hauteur de la grille
     */
    private static void drawLine(
            char[][] grid,
            Coordinate a,
            Coordinate b,
            double maxX,
            double maxY,
            int width,
            int height) {

        int x1 = toGridX(a, maxX, width);
        int y1 = toGridY(a, maxY, height);
        int x2 = toGridX(b, maxX, width);
        int y2 = toGridY(b, maxY, height);

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int err = dx - dy;

        while (true) {
            if (grid[y1][x1] == '.') {
                grid[y1][x1] = '*';
            }

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    /**
     * Convertit une coordonnée en position horizontale sur la grille.
     *
     * @param coordinate coordonnée à convertir
     * @param maxX valeur maximale utilisée pour normaliser l'affichage
     * @param width largeur de la grille
     * @return position horizontale dans la grille
     */
    private static int toGridX(Coordinate coordinate, double maxX, int width) {
        if (maxX == 0) {
            return 0;
        }

        int x = (int) Math.round((coordinate.getLatitude() / maxX) * (width - 1));
        return clamp(x, 0, width - 1);
    }

    /**
     * Convertit une coordonnée en position verticale sur la grille.
     *
     * @param coordinate coordonnée à convertir
     * @param maxY valeur maximale utilisée pour normaliser l'affichage
     * @param height hauteur de la grille
     * @return position verticale dans la grille
     */
    private static int toGridY(Coordinate coordinate, double maxY, int height) {
        if (maxY == 0) {
            return height - 1;
        }

        int y = (int) Math.round((coordinate.getLongitude() / maxY) * (height - 1));
        return clamp(height - 1 - y, 0, height - 1);
    }

    /**
     * Borne une valeur entre un minimum et un maximum.
     *
     * Cette méthode évite les erreurs d'indice lorsque des coordonnées
     * dépassent légèrement les limites de la grille.
     *
     * @param value valeur à borner
     * @param min valeur minimale
     * @param max valeur maximale
     * @return valeur bornée
     */
    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }

    /**
     * Affiche les hôpitaux sur la grille.
     *
     * Les hôpitaux sont affichés après les arêtes afin de rester visibles.
     *
     * @param grid grille ASCII
     * @param hospitals liste des hôpitaux
     * @param maxX valeur maximale en abscisse
     * @param maxY valeur maximale en ordonnée
     * @param width largeur de la grille
     * @param height hauteur de la grille
     */
    private static void drawHospitals(
            char[][] grid,
            List<Hospital> hospitals,
            double maxX,
            double maxY,
            int width,
            int height) {

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital hospital = hospitals.get(i);

            int x = toGridX(hospital.getPosition(), maxX, width);
            int y = toGridY(hospital.getPosition(), maxY, height);

            grid[y][x] = (char) ('1' + i);
        }
    }

    /**
     * Affiche les patients sur la grille.
     *
     * @param grid grille ASCII
     * @param patients liste des patients
     * @param maxX valeur maximale en abscisse
     * @param maxY valeur maximale en ordonnée
     * @param width largeur de la grille
     * @param height hauteur de la grille
     */
    private static void drawPatients(
            char[][] grid,
            List<Patient> patients,
            double maxX,
            double maxY,
            int width,
            int height) {

        for (Patient patient : patients) {
            int x = toGridX(patient.getPosition(), maxX, width);
            int y = toGridY(patient.getPosition(), maxY, height);

            grid[y][x] = 'P';
        }
    }

    /**
     * Affiche la grille ASCII dans la console.
     *
     * @param grid grille à afficher
     */
    private static void printGrid(char[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }

    /**
     * Affiche la légende associant les numéros aux hôpitaux.
     *
     * @param hospitals liste des hôpitaux affichés
     */
    private static void printHospitalLegend(List<Hospital> hospitals) {
        System.out.println();

        for (int i = 0; i < hospitals.size(); i++) {
            System.out.println((i + 1) + " = " + hospitals.get(i).getName()
                    + " " + hospitals.get(i).getPosition());
        }
    }
}