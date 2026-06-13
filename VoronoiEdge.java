import java.util.Objects;

/**
 * Classe métier représentant une arête du diagramme de Voronoï.
 *
 * Une arête de Voronoï est définie par deux coordonnées :
 * un point de départ et un point d'arrivée.
 *
 * Dans le projet, ces arêtes sont calculées à partir de la
 * triangulation de Delaunay et permettent de représenter
 * les frontières entre les zones d'influence des hôpitaux.
 *
 * @author Équipe Projet Emergency Dispatcher
 * @version 1.0
 */
public class VoronoiEdge {

    /**
     * Point de départ de l'arête.
     */
    private Coordinate start;

    /**
     * Point d'arrivée de l'arête.
     */
    private Coordinate end;

    /**
     * Construit une nouvelle arête de Voronoï.
     *
     * @param start point de départ
     * @param end point d'arrivée
     */
    public VoronoiEdge(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Retourne le point de départ de l'arête.
     *
     * @return coordonnée de départ
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * Retourne le point d'arrivée de l'arête.
     *
     * @return coordonnée d'arrivée
     */
    public Coordinate getEnd() {
        return end;
    }

    /**
     * Retourne une représentation textuelle de l'arête.
     *
     * Utile pour l'affichage, les tests et le débogage.
     *
     * @return description complète de l'arête
     */
    @Override
    public String toString() {
        return "VoronoiEdge{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    /**
     * Compare deux arêtes de Voronoï.
     *
     * Deux arêtes sont considérées égales si elles possèdent
     * les mêmes coordonnées de départ et d'arrivée.
     *
     * @param obj objet à comparer
     * @return true si les arêtes sont identiques, false sinon
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof VoronoiEdge)) {
            return false;
        }

        VoronoiEdge other = (VoronoiEdge) obj;

        return Objects.equals(start, other.start)
                && Objects.equals(end, other.end);
    }

    /**
     * Génère un code de hachage cohérent avec equals().
     *
     * Cette méthode permet d'utiliser correctement les objets
     * VoronoiEdge dans les collections de type HashSet ou HashMap.
     *
     * @return code de hachage de l'arête
     */
    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}