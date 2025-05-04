# MarieTeam Client

Projet de cours en BTS SIO - Client Lourd pour la gestion des bateaux/équipements et la génération de brochure pour MarieTeam.

## Description

MarieTeam Client est une application JavaFX permettant de gérer les bateaux et leurs équipements. Elle offre les fonctionnalités suivantes :

- Gestion des bateaux (ajout, modification, suppression, consultation)
- Gestion des équipements (ajout, modification, suppression, consultation)
- Génération de brochures PDF
- Interface utilisateur moderne et intuitive

## Prérequis

- Java JDK 17 ou supérieur
- Maven 3.8 ou supérieur
- MySQL 8.0 ou supérieur
- Git (pour le clonage du projet)

## Installation

1. Cloner le dépôt :
```bash
git clone https://github.com/votre-utilisateur/marieteam-logiciel.git
cd marieteam-logiciel
```

2. Configurer la base de données :
   - Créer une base de données nommée `marieteam`
   - Configurer les paramètres de connexion dans le fichier `.properties`

3. Installer les dépendances :
```bash
mvn clean install
```

## Configuration

Le fichier `.properties` doit contenir les informations suivantes :
```properties
database.url=jdbc:mysql://localhost:3306/marieteam
database.user=votre_utilisateur
database.password=votre_mot_de_passe
```

## Utilisation

1. Lancer l'application :
```bash
mvn javafx:run
```

2. Fonctionnalités principales :
   - Menu Bateaux : Gestion complète des bateaux
   - Menu Équipements : Gestion des équipements
   - Bouton PDF : Génération de brochures

## Structure du Projet

```
src/
├── main/
│   ├── java/
│   │   └── fr/
│   │       └── marieteamclient/
│   │           ├── controllers/    # Contrôleurs JavaFX
│   │           ├── models/         # Modèles de données
│   │           ├── database/       # Accès à la base de données
│   │           ├── constants/      # Constantes de l'application
│   │           └── utils/          # Classes utilitaires
│   └── resources/
│       └── fr/
│           └── marieteamclient/
│               ├── fxml/           # Fichiers FXML
│               └── images/         # Images de l'application
```

## Dépendances

- JavaFX 17.0.6
- iText 8.0.2
- MySQL Connector/J
- ControlsFX
- FormsFX
- ValidatorFX
- Ikonli
- BootstrapFX

## Contribution

1. Fork le projet
2. Créer une branche pour votre fonctionnalité (`git checkout -b feature/AmazingFeature`)
3. Commit vos changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## Contact

Pour toute question ou suggestion, n'hésitez pas à nous contacter :
- Email : contact@marieteam.fr
- Site web : https://www.marieteam.fr
