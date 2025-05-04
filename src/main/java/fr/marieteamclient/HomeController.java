package fr.marieteamclient;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.marieteamclient.constants.Constants;
import fr.marieteamclient.database.DatabaseConnection;
import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;
import fr.marieteamclient.utils.PDFGenerator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Contrôleur principal de l'application, gérant la vue d'accueil et les interactions principales.
 * Cette classe gère :
 * - La vérification de la connexion à la base de données
 * - La génération de PDF
 * - La navigation entre les différentes vues
 * - La gestion des équipements
 */
public class HomeController {

    @FXML
    private Label welcomeText;
    @FXML
    private Label statusLabel;
    @FXML
    private Label equipmentStatusLabel;
    @FXML
    private Button bateauxButton;
    @FXML
    private Button generateButton;
    @FXML
    private Circle connectionStatusIndicator;
    @FXML
    private VBox mainContainer;

    private Timeline connectionCheckTimeline;

    /**
     * Initialise le contrôleur et configure la vérification périodique de la connexion.
     * Cette méthode est appelée automatiquement par JavaFX après le chargement du FXML.
     */
    @FXML
    public void initialize() {
        // Tester la connexion à la base de données immédiatement
        checkDatabaseConnection();
        
        // Configurer la vérification périodique de la connexion
        setupConnectionCheck();
    }

    /**
     * Configure la vérification périodique de la connexion à la base de données.
     * La vérification est effectuée à intervalles réguliers définis dans Constants.DATABASE_CHECK_INTERVAL.
     */
    private void setupConnectionCheck() {
        connectionCheckTimeline = new Timeline(
            new KeyFrame(Duration.millis(Constants.DATABASE_CHECK_INTERVAL), event -> checkDatabaseConnection())
        );
        connectionCheckTimeline.setCycleCount(Animation.INDEFINITE);
        connectionCheckTimeline.play();
    }

    /**
     * Vérifie l'état de la connexion à la base de données et met à jour l'interface utilisateur en conséquence.
     */
    private void checkDatabaseConnection() {
        try {
            DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
            database.getConnection();
            String message = "Connexion à la base de données réussie !";
            boolean isConnected = true;
            
            updateConnectionStatus(isConnected);
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("success");
            statusLabel.setText(message);
        } catch (Exception e) {
            String message = "Erreur de connexion à la base de données : " + e.getMessage();
            boolean isConnected = false;
            
            updateConnectionStatus(isConnected);
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("error");
            statusLabel.setText(message);
        }
    }

    /**
     * Définit la fenêtre principale de l'application.
     * @param stage La fenêtre principale de l'application
     */
    public void setStage(Stage stage) {
        stage.setMinHeight(Constants.APP_HEIGHT);
        stage.setMinWidth(Constants.APP_WIDTH);
    }

    /**
     * Met à jour l'indicateur visuel de l'état de la connexion.
     * @param isConnected État de la connexion (true si connecté, false sinon)
     */
    private void updateConnectionStatus(boolean isConnected) {
        connectionStatusIndicator.getStyleClass().clear();
        if (isConnected) {
            connectionStatusIndicator.getStyleClass().add("connected");
        } else {
            connectionStatusIndicator.getStyleClass().add("disconnected");
        }
    }

    /**
     * Gère l'action de génération du PDF.
     * Permet à l'utilisateur de choisir l'emplacement de sauvegarde et génère le PDF des bateaux.
     */
    @FXML
    public void handleGenerateButton() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.setInitialFileName("BateauVoyageur.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            // Définir le répertoire par défaut sur le dossier Documents
            String userHome = System.getProperty("user.home");
            File defaultDirectory = new File(userHome + "/Documents");
            fileChooser.setInitialDirectory(defaultDirectory);
            
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                // S'assurer que le fichier a l'extension .pdf
                final String filePath = file.getAbsolutePath() + 
                    (file.getAbsolutePath().toLowerCase().endsWith(".pdf") ? "" : ".pdf");
                
                List<Bateau> bateaux = getBateauxFromDatabase();
                List<Equipement> equipements = getEquipementsFromDatabase();

                PDFGenerator.generateBateauxPDF(bateaux, filePath);
                
                // Afficher une alerte de succès avec un bouton pour ouvrir le fichier
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText("PDF généré avec succès");
                successAlert.setContentText("Le fichier a été enregistré dans :\n" + filePath);
                
                // Ajouter un bouton pour ouvrir le fichier
                ButtonType openButton = new ButtonType("Ouvrir");
                ButtonType closeButton = new ButtonType("Fermer");
                successAlert.getButtonTypes().setAll(openButton, closeButton);
                
                // Afficher l'alerte et gérer la réponse
                successAlert.showAndWait().ifPresent(response -> {
                    if (response == openButton) {
                        try {
                            // Ouvrir le fichier avec l'application par défaut
                            java.awt.Desktop.getDesktop().open(new File(filePath));
                        } catch (Exception e) {
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setTitle("Erreur");
                            errorAlert.setHeaderText("Impossible d'ouvrir le fichier");
                            errorAlert.setContentText("Une erreur est survenue lors de l'ouverture du fichier.");
                            errorAlert.showAndWait();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Afficher une alerte d'erreur
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Erreur lors de la génération du PDF");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Récupère la liste des bateaux depuis la base de données.
     * @return Liste des bateaux
     * @throws Exception En cas d'erreur lors de la récupération des données
     */
    private List<Bateau> getBateauxFromDatabase() throws Exception {
        List<Bateau> bateaux = new ArrayList<>();
        DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
        Connection conn = database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM bateau");

        while (rs.next()) {
            Bateau bateau = new Bateau(
                rs.getInt("idBateau"),
                rs.getInt("idCapitaine"),
                rs.getString("nomBateau"),
                rs.getString("marque"),
                rs.getFloat("longueur"),
                rs.getFloat("largeur"),
                rs.getInt("vitesse"),
                rs.getString("image")
            );
            bateaux.add(bateau);
        }

        rs.close();
        stmt.close();
        conn.close();
        return bateaux;
    }

    /**
     * Récupère la liste des équipements depuis la base de données.
     * @return Liste des équipements
     * @throws Exception En cas d'erreur lors de la récupération des données
     */
    private List<Equipement> getEquipementsFromDatabase() throws Exception {
        List<Equipement> equipements = new ArrayList<>();
        DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
        Connection conn = database.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM equipement");

        while (rs.next()) {
            Equipement equipement = new Equipement(
                rs.getInt("idEquipement"),
                rs.getString("labelle")
            );
            equipements.add(equipement);
        }

        rs.close();
        stmt.close();
        conn.close();
        return equipements;
    }

    /**
     * Gère l'action de test de la connexion.
     * Vérifie l'état de la connexion à la base de données et affiche le résultat.
     */
    @FXML
    public void onHelloButtonClick() {
        try {
            DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
            database.getConnection();
            String message = "Connexion à la base de données réussie !";
            boolean isConnected = true;
            
            updateConnectionStatus(isConnected);
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("success");
            statusLabel.setText(message);
        } catch (Exception e) {
            String message = "Erreur de connexion à la base de données : " + e.getMessage();
            boolean isConnected = false;
            
            updateConnectionStatus(isConnected);
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("error");
            statusLabel.setText(message);
        }
    }

    /**
     * Gère l'action d'affichage de la vue "À propos".
     * Charge et affiche la vue about-view.fxml.
     */
    @FXML
    public void handleAbout() {
        try {
            // Charger la vue about
            FXMLLoader loader = new FXMLLoader(getClass().getResource("about-view.fxml"));
            Parent aboutView = loader.load();
            
            // Remplacer le contenu de la fenêtre principale
            Scene scene = statusLabel.getScene();
            scene.setRoot(aboutView);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la vue about : " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Gère l'action de retour à la page d'accueil.
     * Recharge la vue d'accueil en préservant la taille de la fenêtre.
     */
    @FXML
    private void handleHomeButton() {
        try {
            // Recharger la vue d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
            Parent root = loader.load();
            
            // Récupérer la taille actuelle de la fenêtre
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Créer la nouvelle scène avec les styles
            Scene scene = new Scene(root, currentWidth, currentHeight);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            
            // Appliquer la scène en préservant la taille
            stage.setScene(scene);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gère l'action d'affichage de la vue des bateaux.
     * Charge et affiche la vue bateaux.fxml.
     */
    @FXML
    private void handleBateauxButton() {
        try {
            // Charger la vue des bateaux
            FXMLLoader loader = new FXMLLoader(getClass().getResource("bateaux.fxml"));
            Parent root = loader.load();
            
            // Récupérer la taille actuelle de la fenêtre
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            
            // Créer la nouvelle scène avec les styles
            Scene scene = new Scene(root, currentWidth, currentHeight);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            
            // Appliquer la scène en préservant la taille
            stage.setScene(scene);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la vue des bateaux : " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Gère l'action d'ajout d'un équipement.
     * Ouvre une nouvelle fenêtre pour l'ajout d'équipement.
     */
    @FXML
    private void handleAddEquipment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/marieteamclient/equipment-form.fxml"));
            Parent root = loader.load();
            
            EquipmentFormController controller = loader.getController();
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un équipement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gère l'action de modification d'un équipement.
     * Ouvre une nouvelle fenêtre pour la modification d'équipement.
     */
    @FXML
    private void handleEditEquipment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/marieteamclient/equipment-edit.fxml"));
            Parent root = loader.load();
            
            EquipmentEditController controller = loader.getController();
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un équipement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gère l'action de suppression d'un équipement.
     * Ouvre une nouvelle fenêtre pour la suppression d'équipement.
     */
    @FXML
    private void handleDeleteEquipment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/marieteamclient/equipment-delete.fxml"));
            Parent root = loader.load();
            
            EquipmentDeleteController controller = loader.getController();
            controller.setMainController(this);
            
            Stage stage = new Stage();
            stage.setTitle("Supprimer un équipement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche une alerte concernant la gestion des équipements.
     * @param message Le message à afficher
     * @param type Le type d'alerte (success, error, info)
     */
    public void showEquipmentAlert(String message, String type) {
        equipmentStatusLabel.setText(message);
        equipmentStatusLabel.getStyleClass().clear();
        equipmentStatusLabel.getStyleClass().add("equipmentStatusLabel");
        equipmentStatusLabel.getStyleClass().add(type);
        
        // Ajouter l'icône Unicode appropriée
        String icon = "";
        switch (type) {
            case "success":
                icon = "✓ "; // Checkmark
                break;
            case "error":
                icon = "✗ "; // Cross
                break;
            case "info":
                icon = "ℹ "; // Info
                break;
        }
        equipmentStatusLabel.setText(icon + message);
    }

    /**
     * Gère l'action de fermeture de l'application.
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        stage.close();
    }

    /**
     * Gère l'action d'affichage du centre d'aide.
     * Charge et affiche la vue help-view.fxml.
     */
    @FXML
    private void handleHelp() {
        try {
            // Charger la vue d'aide
            FXMLLoader loader = new FXMLLoader(getClass().getResource("help-view.fxml"));
            Parent helpView = loader.load();
            
            // Remplacer le contenu de la fenêtre principale
            Scene scene = statusLabel.getScene();
            scene.setRoot(helpView);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Impossible de charger la vue d'aide : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
