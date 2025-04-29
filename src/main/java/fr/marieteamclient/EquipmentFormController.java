package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import fr.marieteamclient.database.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class EquipmentFormController {
    @FXML
    private TextField equipmentName;
    @FXML
    private Button saveButton;

    private HelloController mainController;

    public void setMainController(HelloController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleSave() {
        try {
            if (equipmentName.getText().isEmpty()) {
                mainController.showEquipmentAlert("Le nom de l'équipement ne peut pas être vide", "error");
                return;
            }

            Connection conn = DatabaseConnection.getConnection();
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

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
} 