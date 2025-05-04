package fr.marieteamclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Contrôleur pour la vue d'aide de l'application.
 * Gère l'affichage de la page d'aide et la navigation vers la page d'accueil.
 */
public class HelpController {

    @FXML
    private VBox mainContainer;

    /**
     * Gère l'événement de clic sur le bouton de retour.
     * Recharge la vue d'accueil et remplace le contenu de la fenêtre principale.
     * 
     * @throws IOException Si une erreur survient lors du chargement de la vue d'accueil
     */
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