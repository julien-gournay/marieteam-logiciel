package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.Node;

public class AboutController {

    @FXML
    private Button backButton;  // Référence au bouton "Retour"

    // Méthode appelée lorsque le bouton "Retour" est cliqué
    @FXML
    public void handleBackButtonClick(ActionEvent event) {
        // Récupère la fenêtre actuelle à partir de l'événement
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }
}
