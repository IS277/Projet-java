/**
 * Classe métier représentant un patient du système.
 *
 * Un patient possède une identité, une position géographique
 * et un type de service médical requis.
 *
 * Cette classe hérite de la classe Person afin de réutiliser
 * les informations communes (identifiant, nom et position).
 *
 * @author Équipe Projet Emergency Dispatcher
 * @version 1.0
 */
public class Patient extends Person {

    /**
     * Service médical dont le patient a besoin.
     */
    private HospitalServiceType requiredService;

    /**
     * Construit un nouveau patient.
     *
     * @param id identifiant unique du patient
     * @param name nom du patient
     * @param position position géographique du patient
     * @param requiredService service médical demandé
     */
    public Patient(
            String id,
            String name,
            Coordinate position,
            HospitalServiceType requiredService
    ) {
        super(id, name, position);
        this.requiredService = requiredService;
    }

    /**
     * Retourne le service médical requis par le patient.
     *
     * Cette information est utilisée lors de l'affectation
     * du patient à un hôpital afin de vérifier que celui-ci
     * possède le service demandé.
     *
     * @return service médical requis
     */
    public HospitalServiceType getRequiredService() {
        return requiredService;
    }

    /**
     * Retourne une représentation textuelle du patient.
     *
     * Utile pour l'affichage, le débogage et les tests.
     *
     * @return description complète du patient
     */
    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", requiredService=" + requiredService +
                '}';
    }
}