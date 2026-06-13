import java.util.List;

/**
 * Utility class responsible for rendering ASCII maps in the console.
 *
 * This class belongs to the presentation layer of the application.
 * It does not modify the business model: it only receives hospitals,
 * patients, Delaunay triangles or Voronoi edges and displays them
 * in a character-based grid.
 *
 * All methods are static because the class does not store any state.
 *
 * <p><b>Class type:</b> Console rendering utility class.</p>
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class AsciiMapRenderer {

    /**
     * Private constructor used to prevent instantiation of this utility class.
     */
    private AsciiMapRenderer() {
    }

    /**
     * Displays hospitals and the Delaunay triangulation in the console.
     *
     * Hospitals are represented by numbers and Delaunay edges are represented
     * with {@code *} characters.
     *
     * @param hospitals hospitals to display
     * @param triangles Delaunay triangles to draw
     * @param width width of the ASCII grid
     * @param height height of the ASCII grid
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

        // Edges are drawn before hospitals so that hospital markers stay visible.
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
     * Displays hospitals, patients and the Delaunay triangulation in the console.
     *
     * @param hospitals hospitals to display
     * @param patients patients to display
     * @param triangles Delaunay triangles to draw
     * @param width width of the ASCII grid
     * @param height height of the ASCII grid
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
     * Displays hospitals and Voronoi edges in the console.
     *
     * @param hospitals hospitals to display
     * @param edges Voronoi edges to draw
     * @param width width of the ASCII grid
     * @param height height of the ASCII grid
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
     * Displays hospitals, patients and Voronoi edges in the console.
     *
     * @param hospitals hospitals to display
     * @param patients patients to display
     * @param edges Voronoi edges to draw
     * @param width width of the ASCII grid
     * @param height height of the ASCII grid
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
     * Creates an ASCII grid initialized with dots.
     *
     * @param width width of the grid
     * @param height height of the grid
     * @return initialized grid
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
     * Draws a line between two coordinates on the grid.
     *
     * This method is used to represent both Delaunay edges and Voronoi edges.
     * It follows the idea of Bresenham's line drawing algorithm.
     *
     * @param grid ASCII grid
     * @param a first coordinate
     * @param b second coordinate
     * @param maxX maximum x value used for scaling
     * @param maxY maximum y value used for scaling
     * @param width width of the grid
     * @param height height of the grid
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
     * Converts a coordinate into a horizontal grid position.
     *
     * @param coordinate coordinate to convert
     * @param maxX maximum x value used for scaling
     * @param width width of the grid
     * @return horizontal position in the grid
     */
    private static int toGridX(Coordinate coordinate, double maxX, int width) {
        if (maxX == 0) {
            return 0;
        }

        int x = (int) Math.round((coordinate.getLatitude() / maxX) * (width - 1));
        return clamp(x, 0, width - 1);
    }

    /**
     * Converts a coordinate into a vertical grid position.
     *
     * @param coordinate coordinate to convert
     * @param maxY maximum y value used for scaling
     * @param height height of the grid
     * @return vertical position in the grid
     */
    private static int toGridY(Coordinate coordinate, double maxY, int height) {
        if (maxY == 0) {
            return height - 1;
        }

        int y = (int) Math.round((coordinate.getLongitude() / maxY) * (height - 1));
        return clamp(height - 1 - y, 0, height - 1);
    }

    /**
     * Clamps a value between a minimum and a maximum.
     *
     * This avoids array index errors when a rendered point is close
     * to the grid boundary.
     *
     * @param value value to clamp
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value
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
     * Draws hospital markers on the grid.
     *
     * Hospitals are drawn after edges so that they remain visible
     * if an edge and a hospital overlap.
     *
     * @param grid ASCII grid
     * @param hospitals hospitals to draw
     * @param maxX maximum x value used for scaling
     * @param maxY maximum y value used for scaling
     * @param width width of the grid
     * @param height height of the grid
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
     * Draws patient markers on the grid.
     *
     * @param grid ASCII grid
     * @param patients patients to draw
     * @param maxX maximum x value used for scaling
     * @param maxY maximum y value used for scaling
     * @param width width of the grid
     * @param height height of the grid
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
     * Prints the ASCII grid in the console.
     *
     * @param grid grid to print
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
     * Prints the legend that associates numbers with hospitals.
     *
     * @param hospitals displayed hospitals
     */
    private static void printHospitalLegend(List<Hospital> hospitals) {
        System.out.println();

        for (int i = 0; i < hospitals.size(); i++) {
            System.out.println((i + 1) + " = " + hospitals.get(i).getName()
                    + " " + hospitals.get(i).getPosition());
        }
    }
}