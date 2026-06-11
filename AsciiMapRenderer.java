import java.util.List;

public class AsciiMapRenderer {

    public static void drawHospitalsAndDelaunay(
            List<Hospital> hospitals,
            List<DelaunayTriangle> triangles,
            int width,
            int height) {
        char[][] grid = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }

        double maxX = 0;
        double maxY = 0;

        for (Hospital hospital : hospitals) {
            maxX = Math.max(maxX, hospital.getPosition().getLatitude());
            maxY = Math.max(maxY, hospital.getPosition().getLongitude());
        }

        for (DelaunayTriangle triangle : triangles) {
            Coordinate[] vertices = triangle.getVertices();

            drawLine(grid, vertices[0], vertices[1], maxX, maxY, width, height);
            drawLine(grid, vertices[1], vertices[2], maxX, maxY, width, height);
            drawLine(grid, vertices[2], vertices[0], maxX, maxY, width, height);
        }

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital hospital = hospitals.get(i);

            int x = toGridX(hospital.getPosition(), maxX, width);
            int y = toGridY(hospital.getPosition(), maxY, height);

            grid[y][x] = (char) ('1' + i);
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }

        System.out.println();

        for (int i = 0; i < hospitals.size(); i++) {
            System.out.println((i + 1) + " = " + hospitals.get(i).getName()
                    + " " + hospitals.get(i).getPosition());
        }
    }

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

    private static int toGridX(Coordinate coordinate, double maxX, int width) {
        return (int) Math.round((coordinate.getLatitude() / maxX) * (width - 1));
    }

    private static int toGridY(Coordinate coordinate, double maxY, int height) {
        int y = (int) Math.round((coordinate.getLongitude() / maxY) * (height - 1));
        return height - 1 - y;
    }

    public static void drawHospitalsPatientsAndDelaunay(
            List<Hospital> hospitals,
            List<Patient> patients,
            List<DelaunayTriangle> triangles,
            int width,
            int height) {
        char[][] grid = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }

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

        for (Patient patient : patients) {
            int x = toGridX(patient.getPosition(), maxX, width);
            int y = toGridY(patient.getPosition(), maxY, height);

            grid[y][x] = 'P';
        }

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital hospital = hospitals.get(i);

            int x = toGridX(hospital.getPosition(), maxX, width);
            int y = toGridY(hospital.getPosition(), maxY, height);

            grid[y][x] = (char) ('1' + i);
        }

        printGrid(grid);

        System.out.println();
        System.out.println("Legend:");
        System.out.println("* = Delaunay edge");
        System.out.println("P = patient");

        for (int i = 0; i < hospitals.size(); i++) {
            System.out.println((i + 1) + " = " + hospitals.get(i).getName()
                    + " " + hospitals.get(i).getPosition());
        }
    }

    private static void printGrid(char[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }

    public static void drawHospitalsAndVoronoi(
            List<Hospital> hospitals,
            List<VoronoiEdge> edges,
            int width,
            int height) {

        char[][] grid = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }

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
            drawLine(
                    grid,
                    edge.getStart(),
                    edge.getEnd(),
                    maxX,
                    maxY,
                    width,
                    height);
        }

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital hospital = hospitals.get(i);

            int x = toGridX(hospital.getPosition(), maxX, width);
            int y = toGridY(hospital.getPosition(), maxY, height);

            grid[y][x] = (char) ('1' + i);
        }

        printGrid(grid);

        System.out.println();
        System.out.println("Legend:");
        System.out.println("* = Voronoi edge");

        for (int i = 0; i < hospitals.size(); i++) {
            System.out.println((i + 1) + " = " + hospitals.get(i).getName()
                    + " " + hospitals.get(i).getPosition());
        }
    }

    public static void drawHospitalsPatientsAndVoronoi(
            List<Hospital> hospitals,
            List<Patient> patients,
            List<VoronoiEdge> edges,
            int width,
            int height) {

        char[][] grid = new char[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }

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
            drawLine(
                    grid,
                    edge.getStart(),
                    edge.getEnd(),
                    maxX,
                    maxY,
                    width,
                    height);
        }

        for (Patient patient : patients) {
            int x = toGridX(patient.getPosition(), maxX, width);
            int y = toGridY(patient.getPosition(), maxY, height);

            grid[y][x] = 'P';
        }

        for (int i = 0; i < hospitals.size(); i++) {
            Hospital hospital = hospitals.get(i);

            int x = toGridX(hospital.getPosition(), maxX, width);
            int y = toGridY(hospital.getPosition(), maxY, height);

            grid[y][x] = (char) ('1' + i);
        }

        printGrid(grid);

        System.out.println();
        System.out.println("Legend:");
        System.out.println("* = Voronoi edge");
        System.out.println("P = patient");

        for (int i = 0; i < hospitals.size(); i++) {
            System.out.println((i + 1) + " = " + hospitals.get(i).getName()
                    + " " + hospitals.get(i).getPosition());
        }
    }
}