package fr.marieteamclient;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.marieteamclient.constants.Constants;
import fr.marieteamclient.database.DatabaseConnection;
import fr.marieteamclient.models.Bateau;
import fr.marieteamclient.models.Equipement;
import fr.marieteamclient.utils.PDFGenerator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Contrôleur principal pour la gestion des bateaux et de leurs équipements.
 * Gère l'affichage, la recherche, l'ajout et la suppression des équipements des bateaux.
 * Permet également la génération de rapports PDF et la vérification de la connexion à la base de données.
 */
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
  private Timeline connectionCheckTimeline;

  private Connection connection;

  /**
   * Initialise le contrôleur.
   * Configure les colonnes de la table, les listes d'équipements,
   * charge les données initiales et configure la recherche.
   */
  @FXML
  public void initialize() {
    DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
    connection = database.getConnection();

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
    loadAllEquipments();

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

    // Configurer la vérification périodique de la connexion
    setupConnectionCheck();
  }

  /**
   * Configure la vérification périodique de la connexion à la base de données.
   * Utilise une Timeline pour vérifier la connexion à intervalles réguliers.
   */
  private void setupConnectionCheck() {
    connectionCheckTimeline = new Timeline(
        new KeyFrame(Duration.millis(Constants.DATABASE_CHECK_INTERVAL), event -> checkDatabaseConnection())
    );
    connectionCheckTimeline.setCycleCount(Animation.INDEFINITE);
    connectionCheckTimeline.play();
  }

  /**
   * Vérifie l'état de la connexion à la base de données.
   * Affiche une alerte en cas d'erreur de connexion.
   */
  private void checkDatabaseConnection() {
    try {
      DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
      database.getConnection();
      String message = "Connexion à la base de données réussie !";
      showAlert("Test de connexion", message, AlertType.INFORMATION);
    } catch (Exception e) {
      String message = "Erreur de connexion à la base de données : " + e.getMessage();
      showAlert("Erreur de connexion", message, AlertType.ERROR);
    }
  }

  /**
   * Charge tous les équipements disponibles depuis la base de données.
   * Met à jour la liste des équipements disponibles dans l'interface.
   */
  private void loadAllEquipments() {
    equipementsData.clear();
    try {
      DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
      Connection conn = database.getConnection();
      
      try {
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM equipement");

        while (rs.next()) {
          Equipement equipement = new Equipement(
              rs.getInt("idEquipement"),
              rs.getString("labelle")
          );
          equipementsData.add(equipement);
        }

        equipementsList.setItems(equipementsData);

        rs.close();
        statement.close();
      } finally {
        conn.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Charge les équipements d'un bateau spécifique.
   * Met à jour la liste des équipements du bateau sélectionné.
   *
   * @param idBateau L'identifiant du bateau dont on veut charger les équipements
   */
  private void loadBateauEquipements(int idBateau) {
    bateauEquipementsData.clear();
    try {
      DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
      Connection conn = database.getConnection();
      
      try {
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
      } finally {
        conn.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gère l'ajout d'équipements au bateau sélectionné.
   * Vérifie les sélections et met à jour la base de données.
   */
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

      // Préparer la requête d'insertion
      PreparedStatement insertStmt = connection.prepareStatement(
          "INSERT INTO possede (idBateau, idEquipement) VALUES (?, ?)"
      );

      int addedCount = 0;
      int alreadyExistsCount = 0;

      for (Equipement equipement : selectedEquipements) {
        // Vérifier si l'équipement est déjà assigné au bateau
        PreparedStatement checkStmt = connection.prepareStatement(
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
        if (!message.isEmpty()) message.append("\n");
        message.append(alreadyExistsCount).append(" équipement(s) était(ent) déjà assigné(s) au bateau.");
      }

      showAlert("Résultat", message.toString(), AlertType.INFORMATION);

      // Recharger les équipements du bateau
      loadBateauEquipements(selectedBateau.getIdBateau());

      insertStmt.close();
      connection.close();
    } catch (Exception e) {
      showAlert("Erreur", "Erreur lors de l'ajout des équipements : " + e.getMessage(), AlertType.ERROR);
      e.printStackTrace();
    }
  }

  /**
   * Gère la suppression d'équipements du bateau sélectionné.
   * Vérifie la sélection et met à jour la base de données.
   */
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

  /**
   * Affiche une boîte de dialogue d'alerte.
   *
   * @param title Le titre de l'alerte
   * @param content Le contenu du message
   * @param type Le type d'alerte
   */
  private void showAlert(String title, String content, AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }

  /**
   * Charge la liste des bateaux depuis la base de données.
   * Met à jour la table des bateaux dans l'interface.
   */
  private void loadBateaux() {
    try {
      DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
      Connection conn = database.getConnection();
      
      try {
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
      } finally {
        conn.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gère la génération du rapport PDF pour les bateaux et leurs équipements.
   * Crée un fichier PDF contenant les informations sur les bateaux.
   */
  @FXML
  private void handleGenerateButton() {
    try {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Enregistrer le PDF");
      fileChooser.setInitialFileName("BateauVoyageur.pdf");
      fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
      );

      String userHome = System.getProperty("user.home");
      File defaultDirectory = new File(userHome + "/Documents");
      fileChooser.setInitialDirectory(defaultDirectory);

      File file = fileChooser.showSaveDialog(null);

      if (file != null) {
        final String filePath = file.getAbsolutePath() +
            (file.getAbsolutePath().toLowerCase().endsWith(".pdf") ? "" : ".pdf");

        List<Bateau> bateaux = getBateauxFromDatabase();
        List<Equipement> equipements = getEquipementsFromDatabase();

        PDFGenerator.generateBateauxPDF(bateaux, filePath);

        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setTitle("Succès");
        successAlert.setHeaderText("PDF généré avec succès");
        successAlert.setContentText("Le fichier a été enregistré dans :\n" + filePath);

        ButtonType openButton = new ButtonType("Ouvrir");
        ButtonType closeButton = new ButtonType("Fermer");
        successAlert.getButtonTypes().setAll(openButton, closeButton);

        successAlert.showAndWait().ifPresent(response -> {
          if (response == openButton) {
            try {
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
   *
   * @return Une liste de bateaux
   * @throws Exception Si une erreur survient lors de la récupération des données
   */
  private List<Bateau> getBateauxFromDatabase() throws Exception {
    List<Bateau> bateaux = new ArrayList<>();
    DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
    Connection conn = database.getConnection();

    try {
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
    } finally {
      conn.close();
    }
    return bateaux;
  }

  /**
   * Récupère la liste des équipements depuis la base de données.
   *
   * @return Une liste d'équipements
   * @throws Exception Si une erreur survient lors de la récupération des données
   */
  private List<Equipement> getEquipementsFromDatabase() throws Exception {
    List<Equipement> equipements = new ArrayList<>();
    DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);

    try (Connection conn = database.getConnection()) {
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
    }
    return equipements;
  }

  @FXML
  private void handleHomeButton() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("home-view.fxml"));
      Parent root = loader.load();

      Stage stage = (Stage) bateauxTable.getScene().getWindow();
      double currentWidth = stage.getWidth();
      double currentHeight = stage.getHeight();

      Scene scene = new Scene(root, currentWidth, currentHeight);
      scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());

      stage.setScene(scene);
      stage.setWidth(currentWidth);
      stage.setHeight(currentHeight);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void handleClose() {
    Stage stage = (Stage) bateauxTable.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void handleAbout() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("about-view.fxml"));
      Parent aboutView = loader.load();

      Scene scene = bateauxTable.getScene();
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

  @FXML
  private void handleHelp() {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Centre d'aide");
    alert.setHeaderText("Aide");
    alert.setContentText("Pour toute assistance, veuillez contacter le support technique.");
    alert.showAndWait();
  }

  @FXML
  private void onHelloButtonClick() {
    try {
      DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
      database.getConnection();
      String message = "Connexion à la base de données réussie !";
      boolean isConnected = true;

      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Test de connexion");
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    } catch (Exception e) {
      String message = "Erreur de connexion à la base de données : " + e.getMessage();
      boolean isConnected = false;

      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Erreur de connexion");
      alert.setHeaderText(null);
      alert.setContentText(message);
      alert.showAndWait();
    }
  }

  @FXML
  private void handleBateauxButton() {
    // Cette méthode est vide car nous sommes déjà dans la vue des bateaux
    // Elle est nécessaire pour éviter l'erreur de chargement FXML
  }

  @FXML
  private void handleAddEquipment() {
    // TODO: Implémenter l'ajout d'équipement
    showAlert("Information", "Fonctionnalité à venir", AlertType.INFORMATION);
  }

  @FXML
  private void handleEditEquipment() {
    // TODO: Implémenter la modification d'équipement
    showAlert("Information", "Fonctionnalité à venir", AlertType.INFORMATION);
  }

  @FXML
  private void handleDeleteEquipment() {
    // TODO: Implémenter la suppression d'équipement
    showAlert("Information", "Fonctionnalité à venir", AlertType.INFORMATION);
  }
} 