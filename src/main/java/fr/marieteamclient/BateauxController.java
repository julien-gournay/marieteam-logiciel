package fr.marieteamclient;

import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
    private ListView<String> equipementsList;

    private ObservableList<Bateau> bateauxData = FXCollections.observableArrayList();
    private ObservableList<String> equipementsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuration des colonnes de la table
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nomBateau"));
        marqueColumn.setCellValueFactory(new PropertyValueFactory<>("marque"));

        // Chargement des données
        loadBateaux();
    }

    private void loadBateaux() {
        try {
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
            
            // Récupération des bateaux
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
            
            bateauxTable.setItems(bateauxData);
            
            // Fermeture des ressources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBateauSelection() {
        Bateau selectedBateau = bateauxTable.getSelectionModel().getSelectedItem();
        if (selectedBateau != null) {
            loadEquipements(selectedBateau.getIdBateau());
        }
    }

    private void loadEquipements(int idBateau) {
        equipementsData.clear();
        try {
            // Connexion à la base de données
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/marieteam", "root", "");
            
            // Récupération des équipements du bateau
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT e.labelle FROM equipement e " +
                "JOIN bateau_equipement be ON e.idEquipement = be.idEquipement " +
                "WHERE be.idBateau = " + idBateau
            );
            
            while (rs.next()) {
                equipementsData.add(rs.getString("labelle"));
            }
            
            equipementsList.setItems(equipementsData);
            
            // Fermeture des ressources
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 