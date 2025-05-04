package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import fr.marieteamclient.models.Equipement;
import fr.marieteamclient.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Contrôleur pour la modification des équipements.
 * Gère l'interface utilisateur permettant de modifier les équipements existants.
 */
public class EquipmentEditController {
    @FXML
    private ComboBox<Equipement> equipmentSelector;
    @FXML
    private TextField newEquipmentName;
    @FXML
    private Button updateButton;

    private ObservableList<Equipement> equipments = FXCollections.observableArrayList();
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
     * Initialise le contrôleur.
     * Cette méthode est appelée automatiquement après le chargement du fichier FXML.
     * Charge la liste des équipements depuis la base de données.
     */
    @FXML
    public void initialize() {
        loadEquipments();
    }

    /**
     * Charge la liste des équipements depuis la base de données.
     * Configure également le ComboBox pour afficher les équipements.
     */
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

    /**
     * Gère l'événement de mise à jour d'un équipement.
     * Vérifie la validité des données, met à jour l'équipement dans la base de données
     * et affiche un message de confirmation.
     */
    @FXML
    private void handleUpdate() {
        try {
            if (equipmentSelector.getValue() == null) {
                mainController.showEquipmentAlert("Veuillez sélectionner un équipement", "error");
                return;
            }

            if (newEquipmentName.getText().isEmpty()) {
                mainController.showEquipmentAlert("Le nouveau nom ne peut pas être vide", "error");
                return;
            }

            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE equipement SET labelle = ? WHERE idEquipement = ?"
            );
            stmt.setString(1, newEquipmentName.getText());
            stmt.setInt(2, equipmentSelector.getValue().getIdEquipement());
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            mainController.showEquipmentAlert("Équipement modifié avec succès", "success");
            
            // Fermer la fenêtre
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            mainController.showEquipmentAlert("Erreur lors de la modification de l'équipement: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    /**
     * Gère l'événement d'annulation.
     * Ferme la fenêtre de modification sans sauvegarder les changements.
     */
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) updateButton.getScene().getWindow();
        stage.close();
    }
} 