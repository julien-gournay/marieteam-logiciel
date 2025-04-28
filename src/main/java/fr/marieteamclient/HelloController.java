package fr.marieteamclient;

import fr.marieteamclient.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    private Label welcomeText;
    @FXML
    private Label statusLabel;

    // Méthode appelée lorsque le bouton "Générer un pdf" est cliqué
    @FXML
    public void onHelloButtonClick() {
        String message = DatabaseConnection.testConnection();
        statusLabel.setText(message);
    }

    // Méthode appelée lorsque le bouton "À propos" est cliqué
    @FXML
    public void handleAboutButtonClick() throws IOException {
        // Charge la vue "À propos"
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("about-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);  // Ajuste les dimensions
        Stage stage = new Stage();
        stage.setTitle("À propos de l'application");
        stage.setScene(scene);
        stage.show();
    }
}
