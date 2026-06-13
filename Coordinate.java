import java.io.Serializable;
import java.util.Objects;

/**
 * Classe métier représentant une coordonnée géographique.
 *
 * Une coordonnée est définie par une latitude et une longitude.
 * Cette classe est utilisée pour positionner les hôpitaux,
 * les patients et les éléments géométriques du projet.
 *
 * @author Équipe Projet Emergency Dispatcher
 * @version 1.0
 */
public class Coordinate implements Serializable {

    /**
     * Latitude de la position.
     */
    private double latitude;

    /**
     * Longitude de la position.
     */
    private double longitude;

    /**
     * Identifiant de version pour la sérialisation.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construit une nouvelle coordonnée.
     *
     * @param latitude latitude de la position
     * @param longitude longitude de la position
     */
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Calcule la distance euclidienne entre cette coordonnée
     * et une autre coordonnée.
     *
     * Cette approximation est suffisante pour les besoins
     * de la simulation.
     *
     * @param c coordonnée de destination
     * @return distance entre les deux positions
     */
    public double distanceTo(Coordinate c) {
        double dx = this.latitude - c.latitude;
        double dy = this.longitude - c.longitude;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @return latitude de la position
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude de la position
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Retourne une représentation textuelle de la coordonnée.
     *
     * @return chaîne décrivant la coordonnée
     */
    @Override
    public String toString() {
        return "Coordinate{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    /**
     * Compare deux coordonnées.
     *
     * Deux coordonnées sont considérées égales si elles
     * possèdent la même latitude et la même longitude.
     *
     * @param obj objet à comparer
     * @return true si les coordonnées sont identiques
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Coordinate)) {
            return false;
        }

        Coordinate other = (Coordinate) obj;

        return Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0;
    }

    /**
     * Génère le code de hachage associé à la coordonnée.
     *
     * @return code de hachage
     */
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
