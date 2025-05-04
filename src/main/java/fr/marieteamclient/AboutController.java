package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AboutController {

    @FXML
    private VBox mainContainer;

    // Méthode appelée lorsque le bouton "Retour" est cliqué
    @FXML
    private void handleBackButtonClick() {
        try {
            // Recharger la vue d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/marieteamclient/home-view.fxml"));
            Parent root = loader.load();
            
            // Remplacer le contenu de la fenêtre principale
            Scene scene = mainContainer.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
