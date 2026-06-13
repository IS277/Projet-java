# Projet Voronoï / Delaunay — Orientation vers un hôpital adapté

## Description du projet

Ce projet a pour objectif de développer une application permettant d’orienter un patient vers l’hôpital le plus adapté à sa situation médicale.

L’application repose sur une carte 2D et utilise une approche basée sur le diagramme de Voronoï et la triangulation de Delaunay.

Les hôpitaux sont représentés comme des sites Voronoï, tandis que les patients ou lieux d’urgence sont représentés comme des points utilisateurs.

## Objectif

L’objectif n’est pas seulement de trouver l’hôpital le plus proche géographiquement, mais de proposer l’hôpital le plus adapté selon plusieurs critères :

- la position du patient ;
- le mode de transport ;
- le temps de trajet ;
- le niveau de saturation des hôpitaux ;
- le service médical nécessaire ;
- la disponibilité de l’hôpital.

## Cas d’usage principal

Le cas d’usage principal est :

**Orienter un patient vers l’hôpital le plus adapté.**

Exemple : un ambulancier intervient auprès d’un patient présentant des signes d’AVC. Il place le patient sur la carte, choisit le mode de transport et indique le service médical nécessaire, par exemple la neurologie. L’application compare ensuite les hôpitaux selon leur distance, leur saturation et leurs services disponibles, puis recommande l’hôpital le plus adapté.

## Approche Voronoï

Dans ce projet :

- les sites Voronoï représentent les hôpitaux ou centres d’urgence ;
- les points utilisateurs représentent les patients ou lieux d’urgence ;
- les zones de Voronoï représentent les zones de couverture des hôpitaux ;
- le système associe chaque patient à l’hôpital le plus pertinent.

## Fonctionnalités prévues

- Ajouter un patient sur la carte
- Ajouter ou gérer des hôpitaux
- Choisir un mode de transport
- Choisir un service médical nécessaire
- Calculer les distances et les temps de trajet
- Vérifier la saturation des hôpitaux
- Exclure les hôpitaux non adaptés
- Sélectionner l’hôpital le plus pertinent
- Afficher le chemin recommandé
- Afficher la zone Voronoï associée
- Consulter les statistiques des zones
- Importer une carte
- Exporter une carte

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
- UML
- Diagramme de Voronoï
- Triangulation de Delaunay

