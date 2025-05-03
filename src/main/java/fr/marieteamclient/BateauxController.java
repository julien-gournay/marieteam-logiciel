package fr.marieteamclient;

import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class BateauxController {
    @FXML
    private TableView<Bateau> bateauxTable;
    @FXML
    private TableColumn<Bateau, String> nomColumn;
    @FXML
    private TableColumn<Bateau, String> marqueColumn;
    @FXML
    private TableColumn<Bateau, Float> largeurColumn;
    @FXML
    private TableColumn<Bateau, Float> longueurColumn;
    @FXML
    private TableColumn<Bateau, Integer> vitesseColumn;
    @FXML
    private ListView<Equipement> equipementsList;
    @FXML
    private ListView<Equipement> bateauEquipementsList;
    @FXML
    private TextField searchField;

    private ObservableList<Bateau> bateauxData = FXCollections.observableArrayList();
    private ObservableList<Equipement> equipementsData = FXCollections.observableArrayList();
    private ObservableList<Equipement> bateauEquipementsData = FXCollections.observableArrayList();
    private FilteredList<Bateau> filteredData;
    private Bateau selectedBateau;

    @FXML
    public void initialize() {
        // Configuration des colonnes de la table
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomBateau"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));
        largeurColumn.setCellValueFactory(new PropertyValueFactory<>("largeur"));
        longueurColumn.setCellValueFactory(new PropertyValueFactory<>("longueur"));
        vitesseColumn.setCellValueFactory(new PropertyValueFactory<>("vitesse"));

        // Configuration de la ListView des équipements disponibles
        equipementsList.setCellFactory(lv -> new ListCell<Equipement>() {
            @Override
            protected void updateItem(Equipement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getLabelle());
            }
        });

        // Configuration de la ListView des équipements du bateau
        bateauEquipementsList.setCellFactory(lv -> new ListCell<Equipement>() {
            @Override
            protected void updateItem(Equipement item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getLabelle());
            }
        });

        // Configuration de la sélection multiple pour les équipements disponibles
        equipementsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Configuration de la sélection des bateaux
        bateauxTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBateau = newSelection;
                loadBateauEquipements(newSelection.getIdBateau());
            } else {
                selectedBateau = null;
                bateauEquipementsData.clear();
            }
        });

        // Chargement des données
        loadBateaux();
        loadAllEquipements();

        // Configuration de la recherche
        filteredData = new FilteredList<>(bateauxData, b -> true);
        
        // Lier la recherche au champ de texte
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(bateau -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return bateau.getNomBateau().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Créer une liste triée et la lier à la table
        SortedList<Bateau> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(bateauxTable.comparatorProperty());
        bateauxTable.setItems(sortedData);
    }

    private void loadAllEquipements() {
        equipementsData.clear();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM equipement");
            
            while (rs.next()) {
                Equipement equipement = new Equipement(
                    rs.getInt("idEquipement"),
                    rs.getString("labelle")
                );
                equipementsData.add(equipement);
            }
            
            equipementsList.setItems(equipementsData);
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBateauEquipements(int idBateau) {
        bateauEquipementsData.clear();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT e.* FROM equipement e " +
                "JOIN possede p ON e.idEquipement = p.idEquipement " +
                "WHERE p.idBateau = " + idBateau
            );
            
            while (rs.next()) {
                bateauEquipementsData.add(new Equipement(
                    rs.getInt("idEquipement"),
                    rs.getString("labelle")
                ));
            }
            
            bateauEquipementsList.setItems(bateauEquipementsData);
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEquipmentToBoat() {
        if (selectedBateau == null) {
            showAlert("Erreur", "Veuillez sélectionner un bateau", AlertType.ERROR);
            return;
        }

        ObservableList<Equipement> selectedEquipements = equipementsList.getSelectionModel().getSelectedItems();
        if (selectedEquipements.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner au moins un équipement", AlertType.ERROR);
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
            
            // Préparer la requête d'insertion
            PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO possede (idBateau, idEquipement) VALUES (?, ?)"
            );
            
            int addedCount = 0;
            int alreadyExistsCount = 0;
            
            for (Equipement equipement : selectedEquipements) {
                // Vérifier si l'équipement est déjà assigné au bateau
                PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM possede WHERE idBateau = ? AND idEquipement = ?"
                );
                checkStmt.setInt(1, selectedBateau.getIdBateau());
                checkStmt.setInt(2, equipement.getIdEquipement());
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                
                if (rs.getInt(1) == 0) {
                    // Ajouter l'équipement au bateau
                    insertStmt.setInt(1, selectedBateau.getIdBateau());
                    insertStmt.setInt(2, equipement.getIdEquipement());
                    insertStmt.executeUpdate();
                    addedCount++;
                } else {
                    alreadyExistsCount++;
                }
                
                checkStmt.close();
            }

            // Afficher un message récapitulatif
            StringBuilder message = new StringBuilder();
            if (addedCount > 0) {
                message.append(addedCount).append(" équipement(s) ajouté(s) avec succès.");
            }
            if (alreadyExistsCount > 0) {
                if (message.length() > 0) message.append("\n");
                message.append(alreadyExistsCount).append(" équipement(s) était(ent) déjà assigné(s) au bateau.");
            }
            
            showAlert("Résultat", message.toString(), AlertType.INFORMATION);
            
            // Recharger les équipements du bateau
            loadBateauEquipements(selectedBateau.getIdBateau());
            
            insertStmt.close();
            conn.close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout des équipements : " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveEquipmentFromBoat() {
        if (selectedBateau == null) {
            showAlert("Erreur", "Veuillez sélectionner un bateau", AlertType.ERROR);
            return;
        }

        ObservableList<Equipement> selectedEquipements = bateauEquipementsList.getSelectionModel().getSelectedItems();
        if (selectedEquipements.isEmpty()) {
            showAlert("Erreur", "Veuillez sélectionner au moins un équipement à supprimer", AlertType.ERROR);
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
            
            // Préparer la requête de suppression
            PreparedStatement deleteStmt = conn.prepareStatement(
                "DELETE FROM possede WHERE idBateau = ? AND idEquipement = ?"
            );
            
            int removedCount = 0;
            
            for (Equipement equipement : selectedEquipements) {
                deleteStmt.setInt(1, selectedBateau.getIdBateau());
                deleteStmt.setInt(2, equipement.getIdEquipement());
                removedCount += deleteStmt.executeUpdate();
            }

            // Afficher un message de confirmation
            showAlert("Succès", removedCount + " équipement(s) supprimé(s) avec succès.", AlertType.INFORMATION);
            
            // Recharger les équipements du bateau
            loadBateauEquipements(selectedBateau.getIdBateau());
            
            deleteStmt.close();
            conn.close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression des équipements : " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadBateaux() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
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
                bateauxData.add(bateau);
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 