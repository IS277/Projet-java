# Projet Voronoï / Delaunay — Orientation vers un hôpital adapté

## Description du projet

Ce projet a pour objectif de développer une application permettant d'orienter un patient vers l'hôpital le plus adapté à sa situation médicale.

L'application repose sur une carte 2D et utilise une approche basée sur le diagramme de Voronoï et la triangulation de Delaunay.
Les hôpitaux sont représentés comme des sites Voronoï, tandis que les patients ou lieux d'urgence sont représentés comme des points utilisateurs.

## Objectif

L'objectif n'est pas seulement de trouver l'hôpital le plus proche géographiquement, mais de proposer l'hôpital le plus adapté selon plusieurs critères :

- la position du patient ;
- le niveau de saturation des hôpitaux ;
- le service médical nécessaire ;
- la disponibilité de l'hôpital.

## Cas d'usage principal

Le cas d'usage principal est :

**Orienter un patient vers l'hôpital le plus adapté.**

Exemple : un ambulancier intervient auprès d'un patient présentant des signes d'AVC. Il place le patient sur la carte et indique le service médical nécessaire, par exemple la neurologie. L'application compare ensuite les hôpitaux selon leur distance, leur saturation et leurs services disponibles, puis recommande l'hôpital le plus adapté.

## Approche Voronoï

Dans ce projet :

- les sites Voronoï représentent les hôpitaux ;
- les points utilisateurs représentent les patients ou lieux d'urgence ;
- les zones de Voronoï représentent les zones de couverture des hôpitaux ;
- le système associe chaque patient à l'hôpital le plus pertinent via l'`AssignmentService`.

## Fonctionnalités implémentées

### Gestion des hôpitaux
- Ajouter, supprimer ou déplacer un hôpital sur la carte
- Afficher la liste des hôpitaux
- Importer des hôpitaux depuis un fichier CSV

### Gestion des patients
- Ajouter, supprimer ou déplacer un patient
- Afficher la liste des patients
- Ajouter des patients aléatoirement
- Afficher l'hôpital assigné à un patient

### Visualisation
- Afficher la triangulation de Delaunay
- Afficher les zones de Voronoï
- Cartes ASCII : Delaunay, Delaunay + patients, arêtes Voronoï, Voronoï + patients

### Inspection
- Inspecter un triangle Delaunay
- Inspecter une zone Voronoï

### Persistance
- Sauvegarder et charger une carte (sérialisation binaire via `ObjectOutputStream`)

### Recalcul automatique
- Recalcul automatique de la triangulation de Delaunay et des zones Voronoï à chaque ajout, suppression ou déplacement d'un hôpital ou d'un patient

## Architecture

Le projet est structuré en trois couches distinctes :

- **Classes métier** (`Hospital`, `Patient`, `Coordinate`, `Delaunay`, `VoronoiService`, `AssignmentService`, etc.) — logique applicative et algorithmes géométriques
- **Interface graphique** (`MainMenu` JavaFX) — visualisation et interaction utilisateur
- **Interface ligne de commande** (`CommandLineApp`) — point d'entrée alternatif en mode console

La séparation entre les classes métier et les classes d'interface graphique a été un axe central du développement, afin d'éviter les duplications de logique et d'assurer la maintenabilité du projet.

## Acteurs du système

### Acteurs primaires
- Patient
- Ambulancier
- Chef de ville
- Municipalité

### Acteurs secondaires
- Base hospitalière
- Système de fichiers

## Technologies utilisées

- Java
- JavaFX
- UML (diagrammes de classes, Use Case)
- Algorithme de Voronoï
- Triangulation de Delaunay

