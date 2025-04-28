package fr.marieteamclient;

import fr.marieteamclient.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;  // Référence au Label pour le texte de bienvenue

    @FXML
    private Label statusLabel;  // Référence au Label pour le message de connexion

    // Méthode appelée lorsque le bouton est cliqué
    @FXML
    public void onHelloButtonClick() {
        // Appel de la méthode pour tester la connexion
        String message = DatabaseConnection.testConnection();

        // Mise à jour du texte du Label avec le message de connexion
        statusLabel.setText(message);
    }

}