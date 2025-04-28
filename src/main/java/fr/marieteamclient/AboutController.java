package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class AboutController {

    @FXML
    private Button backButton;  // Référence au bouton "Retour"

    // Méthode appelée lorsque le bouton "Retour" est cliqué
    @FXML
    public void handleBackButtonClick() {
        // Ferme la fenêtre actuelle en récupérant le stage via le bouton
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close(); // Ferme la fenêtre actuelle
    }
}
