import java.util.Objects;

/**
 * Classe géométrique représentant un triangle de la triangulation de Delaunay.
 *
 * Un triangle est défini par trois sommets de type {@link Coordinate}.
 * Cette classe fournit les méthodes nécessaires pour construire et analyser
 * la triangulation :
 * calcul du cercle circonscrit, du centre du cercle, de la surface et des
 * distances entre sommets.
 *
 * @author Équipe Projet Emergency Dispatcher
 * @version 1.0
 */
public class DelaunayTriangle {

    /**
     * Sommets du triangle.
     */
    private Coordinate[] vertices;

    /**
     * Construit un triangle à partir de trois coordonnées.
     *
     * @param a premier sommet du triangle
     * @param b deuxième sommet du triangle
     * @param c troisième sommet du triangle
     */
    public DelaunayTriangle(Coordinate a, Coordinate b, Coordinate c) {
        this.vertices = new Coordinate[] { a, b, c };
    }

    /**
     * Vérifie si une coordonnée se trouve à l'intérieur du cercle circonscrit
     * au triangle.
     *
     * Cette méthode est essentielle dans l'algorithme de Bowyer-Watson :
     * si un nouveau point est dans le cercle circonscrit d'un triangle,
     * ce triangle ne respecte plus la propriété de Delaunay.
     *
     * @param p coordonnée à tester
     * @return true si la coordonnée est dans le cercle circonscrit
     */
    public boolean isInCircumcircle(Coordinate p) {

        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();
        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();
        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        // Le centre du cercle circonscrit est équidistant des trois sommets.
        double d = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));

        double ux = ((ax * ax + ay * ay) * (by - cy)
                + (bx * bx + by * by) * (cy - ay)
                + (cx * cx + cy * cy) * (ay - by)) / d;

        double uy = ((ax * ax + ay * ay) * (cx - bx)
                + (bx * bx + by * by) * (ax - cx)
                + (cx * cx + cy * cy) * (bx - ax)) / d;

        double radius = Math.sqrt((ax - ux) * (ax - ux) + (ay - uy) * (ay - uy));

        double dist = Math.sqrt((p.getLatitude() - ux) * (p.getLatitude() - ux)
                + (p.getLongitude() - uy) * (p.getLongitude() - uy));

        return dist < radius;
    }

    /**
     * Retourne les trois sommets du triangle.
     *
     * @return tableau contenant les sommets du triangle
     */
    public Coordinate[] getVertices() {
        return vertices;
    }

    /**
     * Calcule le centre du cercle circonscrit au triangle.
     *
     * Ce point devient un sommet du diagramme de Voronoï.
     *
     * @return centre du cercle circonscrit
     */
    public Coordinate getCircumcenter() {

        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();

        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();

        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        double d = 2 * (ax * (by - cy)
                + bx * (cy - ay)
                + cx * (ay - by));

        double ux = ((ax * ax + ay * ay) * (by - cy)
                + (bx * bx + by * by) * (cy - ay)
                + (cx * cx + cy * cy) * (ay - by))
                / d;

        double uy = ((ax * ax + ay * ay) * (cx - bx)
                + (bx * bx + by * by) * (ax - cx)
                + (cx * cx + cy * cy) * (bx - ax))
                / d;

        return new Coordinate(ux, uy);
    }

    /**
     * Indique si deux triangles partagent une arête.
     *
     * Deux triangles partagent une arête lorsqu'ils ont exactement
     * deux sommets en commun.
     *
     * @param other autre triangle à comparer
     * @return true si les deux triangles partagent une arête
     */
    public boolean sharesEdgeWith(DelaunayTriangle other) {
        int commonVertices = 0;

        for (Coordinate vertex : this.vertices) {
            for (Coordinate otherVertex : other.getVertices()) {
                if (vertex.equals(otherVertex)) {
                    commonVertices++;
                }
            }
        }

        return commonVertices == 2;
    }

    /**
     * Vérifie si le triangle contient la position d'un hôpital.
     *
     * @param hospital hôpital à tester
     * @return true si la position de l'hôpital est un sommet du triangle
     */
    public boolean containsHospital(Hospital hospital) {
        Coordinate position = hospital.getPosition();

        for (Coordinate vertex : vertices) {
            if (vertex.equals(position)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Calcule la surface du triangle.
     *
     * @return surface du triangle
     */
    public double getSurface() {
        double ax = vertices[0].getLatitude();
        double ay = vertices[0].getLongitude();

        double bx = vertices[1].getLatitude();
        double by = vertices[1].getLongitude();

        double cx = vertices[2].getLatitude();
        double cy = vertices[2].getLongitude();

        return Math.abs(
                ax * (by - cy)
                        + bx * (cy - ay)
                        + cx * (ay - by))
                / 2.0;
    }

    /**
     * Calcule la distance entre le premier et le deuxième sommet.
     *
     * @return distance entre A et B
     */
    public double getDistanceAB() {
        return vertices[0].distanceTo(vertices[1]);
    }

    /**
     * Calcule la distance entre le deuxième et le troisième sommet.
     *
     * @return distance entre B et C
     */
    public double getDistanceBC() {
        return vertices[1].distanceTo(vertices[2]);
    }

    /**
     * Calcule la distance entre le troisième et le premier sommet.
     *
     * @return distance entre C et A
     */
    public double getDistanceCA() {
        return vertices[2].distanceTo(vertices[0]);
    }

    /**
     * Retourne une représentation textuelle du triangle.
     *
     * @return description du triangle
     */
    @Override
    public String toString() {
        return "Triangle[" + vertices[0] + ", " + vertices[1] + ", " + vertices[2] + "]";
    }

    /**
     * Compare deux triangles.
     *
     * Deux triangles sont considérés égaux s'ils possèdent les mêmes sommets
     * dans le même ordre.
     *
     * @param obj objet à comparer
     * @return true si les deux triangles sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DelaunayTriangle)) {
            return false;
        }

        DelaunayTriangle other = (DelaunayTriangle) obj;

        return vertices[0].equals(other.vertices[0])
                && vertices[1].equals(other.vertices[1])
                && vertices[2].equals(other.vertices[2]);
    }

    /**
     * Génère le code de hachage du triangle.
     *
     * @return code de hachage
     */
    @Override
    public int hashCode() {
        return Objects.hash(vertices[0], vertices[1], vertices[2]);
    }
}