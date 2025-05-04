package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import fr.marieteamclient.models.Equipement;
import fr.marieteamclient.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EquipmentDeleteController {
    @FXML
    private ComboBox<Equipement> equipmentSelector;
    @FXML
    private Button deleteButton;

    private ObservableList<Equipement> equipments = FXCollections.observableArrayList();
    private HomeController mainController;

    public void setMainController(HomeController controller) {
        this.mainController = controller;
    }

    @FXML
    public void initialize() {
        loadEquipments();
    }

    private void loadEquipments() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM equipement");

            while (rs.next()) {
                Equipement equipement = new Equipement(
                    rs.getInt("idEquipement"),
                    rs.getString("labelle")
                );
                equipments.add(equipement);
            }

            equipmentSelector.setItems(equipments);
            equipmentSelector.setConverter(new javafx.util.StringConverter<Equipement>() {
                @Override
                public String toString(Equipement equipement) {
                    return equipement.getLabelle();
                }

                @Override
                public Equipement fromString(String string) {
                    return null;
                }
            });

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        try {
            if (equipmentSelector.getValue() == null) {
                mainController.showEquipmentAlert("Veuillez sélectionner un équipement", "error");
                return;
            }

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM equipement WHERE idEquipement = ?"
            );
            stmt.setInt(1, equipmentSelector.getValue().getIdEquipement());
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            mainController.showEquipmentAlert("Équipement supprimé avec succès", "success");
            
            // Fermer la fenêtre
            Stage stage = (Stage) deleteButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            mainController.showEquipmentAlert("Erreur lors de la suppression de l'équipement: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) deleteButton.getScene().getWindow();
        stage.close();
    }
} 