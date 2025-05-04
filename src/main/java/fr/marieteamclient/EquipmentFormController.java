package fr.marieteamclient;

import java.sql.Connection;
import java.sql.PreparedStatement;

import fr.marieteamclient.constants.Constants;
import fr.marieteamclient.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Contrôleur pour le formulaire d'ajout d'équipement.
 * Gère l'interface utilisateur permettant d'ajouter un nouvel équipement à la base de données.
 */
public class EquipmentFormController {
    @FXML
    private TextField equipmentName;
    @FXML
    private Button saveButton;

    private HomeController mainController;

    /**
     * Définit le contrôleur principal de l'application.
     * Permet la communication entre les contrôleurs.
     *
     * @param controller Le contrôleur principal de l'application
     */
    public void setMainController(HomeController controller) {
        this.mainController = controller;
    }

    /**
     * Gère l'événement de sauvegarde d'un nouvel équipement.
     * Vérifie la validité des données, enregistre l'équipement dans la base de données
     * et affiche un message de confirmation.
     */
    @FXML
    private void handleSave() {
        try {
            if (equipmentName.getText().isEmpty()) {
                mainController.showEquipmentAlert("Le nom de l'équipement ne peut pas être vide", "error");
                return;
            }

            DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO equipement (labelle) VALUES (?)"
            );
            stmt.setString(1, equipmentName.getText());
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            mainController.showEquipmentAlert("Équipement ajouté avec succès", "success");
            
            // Fermer la fenêtre
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            mainController.showEquipmentAlert("Erreur lors de l'ajout de l'équipement: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    /**
     * Gère l'événement d'annulation.
     * Ferme la fenêtre du formulaire sans sauvegarder.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
} 