package fr.marieteamclient;

import fr.marieteamclient.database.DatabaseConnection;
import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;
import fr.marieteamclient.utils.PDFGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.scene.shape.Circle;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HelloController {

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

    @FXML
    public void initialize() {
        // État initial de la connexion
        updateConnectionStatus(false);
    }

    public void setStage(Stage stage) {
        stage.setMinHeight(700);
        stage.setMinWidth(800);
    }

    private void updateConnectionStatus(boolean isConnected) {
        connectionStatusIndicator.getStyleClass().clear();
        if (isConnected) {
            connectionStatusIndicator.getStyleClass().add("connected");
        } else {
            connectionStatusIndicator.getStyleClass().add("disconnected");
        }
    }

    // Méthode appelée lorsque le bouton "Générer un pdf" est cliqué
    @FXML
    public void handleGenerateButton() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                List<Bateau> bateaux = getBateauxFromDatabase();
                List<Equipement> equipements = getEquipementsFromDatabase();

                PDFGenerator.generateBateauxPDF(bateaux, file.getAbsolutePath());
                statusLabel.getStyleClass().clear();
                statusLabel.getStyleClass().add("success");
                statusLabel.setText("PDF généré avec succès !");
            }
        } catch (Exception e) {
            statusLabel.getStyleClass().clear();
            statusLabel.getStyleClass().add("error");
            statusLabel.setText("Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Bateau> getBateauxFromDatabase() throws Exception {
        List<Bateau> bateaux = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
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
                rs.getInt("vitesse")
            );
            bateaux.add(bateau);
        }

        rs.close();
        stmt.close();
        conn.close();
        return bateaux;
    }

    private List<Equipement> getEquipementsFromDatabase() throws Exception {
        List<Equipement> equipements = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
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

    // Méthode appelée lorsque le bouton "Tester la connexion" est cliqué
    @FXML
    public void onHelloButtonClick() {
        String message = DatabaseConnection.testConnection();
        boolean isConnected = message.contains("réussie");
        
        updateConnectionStatus(isConnected);
        statusLabel.getStyleClass().clear();
        if (isConnected) {
            statusLabel.getStyleClass().add("success");
        } else {
            statusLabel.getStyleClass().add("error");
        }
        statusLabel.setText(message);
    }

    // Méthode appelée lorsque le bouton "À propos" est cliqué
    @FXML
    public void handleAboutButtonClick() throws IOException {
        // Charge la vue "À propos"
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);  // Ajuste les dimensions
        Stage stage = new Stage();
        stage.setTitle("À propos de l'application");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleBateauxButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/marieteamclient/bateaux.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Liste des Bateaux et Équipements");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}
