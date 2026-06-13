import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import java.io.Serializable;

/**
 * Représente un hôpital du système d'urgence.
 *
 * Cette classe métier contient toutes les informations nécessaires
 * à la gestion d'un hôpital :
 * <ul>
 *     <li>son identifiant unique ;</li>
 *     <li>son nom ;</li>
 *     <li>sa position géographique ;</li>
 *     <li>sa capacité maximale ;</li>
 *     <li>sa capacité actuellement utilisée ;</li>
 *     <li>les services médicaux qu'il propose.</li>
 * </ul>
 *
 * La classe implémente {@link Serializable} afin de permettre
 * la sauvegarde et le chargement de la carte via
 * MapPersistenceService.
 *
 * Deux hôpitaux sont considérés identiques s'ils possèdent
 * le même identifiant.
 *
 * @author Maïssa Tirsane, Anas Chokri, Iyed Souissi, Valery Vo-Van
 * @version 1.0
 */
public class Hospital implements Serializable {

    /**
     * Identifiant unique de l'hôpital.
     */
    private String id;

    /**
     * Nom affiché de l'hôpital.
     */
    private String name;

    /**
     * Position géographique de l'hôpital.
     */
    private Coordinate position;

    /**
     * Nombre maximal de patients pouvant être accueillis.
     */
    private int maxCapacity;

    /**
     * Nombre actuel de patients pris en charge.
     */
    private int currentCapacity;

    /**
     * Ensemble des services médicaux disponibles.
     */
    private Set<HospitalServiceType> services;

    /**
     * Identifiant de version utilisé lors de la sérialisation.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construit un nouvel hôpital.
     *
     * La capacité actuelle est initialisée à 0 et
     * aucun service n'est ajouté automatiquement.
     *
     * @param id identifiant unique de l'hôpital
     * @param name nom de l'hôpital
     * @param position position géographique
     * @param maxCapacity capacité maximale d'accueil
     */
    public Hospital(
            String id,
            String name,
            Coordinate position,
            int maxCapacity) {

        this.id = id;
        this.name = name;
        this.position = position;
        this.maxCapacity = maxCapacity;

        // Un hôpital est vide lors de sa création.
        this.currentCapacity = 0;

        // Les services seront ajoutés ultérieurement.
        this.services = new HashSet<>();
    }

    /**
     * Calcule le taux de saturation de l'hôpital.
     *
     * Cette valeur est comprise entre 0 et 1.
     * Elle est utilisée dans l'affichage graphique
     * ainsi que dans les statistiques.
     *
     * @return taux de saturation
     */
    public double getSaturationRate() {
        return (double) currentCapacity / maxCapacity;
    }

    /**
     * Indique si l'hôpital est plein.
     *
     * @return true si la capacité maximale est atteinte
     */
    public boolean isSaturated() {
        return currentCapacity >= maxCapacity;
    }

    /**
     * Met à jour le nombre de patients actuellement admis.
     *
     * Afin de garantir la cohérence de l'objet,
     * la valeur est automatiquement bornée entre
     * 0 et la capacité maximale.
     *
     * @param currentCapacity nouvelle capacité utilisée
     */
    public void updateCapacity(int currentCapacity) {

        if (currentCapacity < 0) {
            this.currentCapacity = 0;
            return;
        }

        if (currentCapacity > maxCapacity) {
            this.currentCapacity = maxCapacity;
            return;
        }

        this.currentCapacity = currentCapacity;
    }

    /**
     * Ajoute un service médical à l'hôpital.
     *
     * L'utilisation d'un Set empêche automatiquement
     * les doublons.
     *
     * @param service service à ajouter
     */
    public void addService(HospitalServiceType service) {
        services.add(service);
    }

    /**
     * Vérifie si l'hôpital propose un service donné.
     *
     * Cette méthode est utilisée par
     * AssignmentService lors de la recherche
     * du meilleur hôpital pour un patient.
     *
     * @param service service recherché
     * @return true si le service est disponible
     */
    public boolean hasService(HospitalServiceType service) {
        return services.contains(service);
    }

    /**
     * @return identifiant de l'hôpital
     */
    public String getId() {
        return id;
    }

    /**
     * @return nom de l'hôpital
     */
    public String getName() {
        return name;
    }

    /**
     * @return position géographique
     */
    public Coordinate getPosition() {
        return position;
    }

    /**
     * @return capacité maximale
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * @return capacité actuellement utilisée
     */
    public int getCurrentCapacity() {
        return currentCapacity;
    }

    /**
     * Retourne une copie défensive de l'ensemble des services.
     *
     * Cela évite qu'une autre classe puisse modifier
     * directement la collection interne de l'hôpital.
     *
     * @return copie des services proposés
     */
    public Set<HospitalServiceType> getServices() {
        return new HashSet<>(services);
    }

    /**
     * Met à jour la position de l'hôpital.
     *
     * Utilisé notamment lors du déplacement
     * d'un hôpital dans l'interface graphique.
     *
     * @param newPosition nouvelle position
     */
    public void updatePosition(Coordinate newPosition) {
        this.position = newPosition;
    }

    /**
     * Tente d'admettre un nouveau patient.
     *
     * La capacité est augmentée uniquement
     * si l'hôpital n'est pas saturé.
     *
     * @return true si le patient a été admis
     */
    public boolean admitPatient() {

        if (isSaturated()) {
            return false;
        }

        currentCapacity++;
        return true;
    }

    /**
     * Retourne une représentation textuelle complète
     * de l'hôpital.
     *
     * Utile pour le débogage et l'affichage console.
     *
     * @return description complète de l'hôpital
     */
    @Override
    public String toString() {
        return "Hospital{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", maxCapacity=" + maxCapacity +
                ", currentCapacity=" + currentCapacity +
                ", services=" + services +
                '}';
    }

    /**
     * Deux hôpitaux sont égaux lorsqu'ils possèdent
     * le même identifiant.
     *
     * @param obj objet à comparer
     * @return true si les identifiants sont identiques
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Hospital)) {
            return false;
        }

        Hospital other = (Hospital) obj;

        return Objects.equals(id, other.id);
    }

    /**
     * Génère le code de hachage associé à l'identifiant.
     *
     * Cette méthode doit être cohérente avec equals()
     * pour permettre l'utilisation correcte de Hospital
     * dans les HashSet et HashMap.
     *
     * @return code de hachage
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}