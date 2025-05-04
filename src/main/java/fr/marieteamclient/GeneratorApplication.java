package fr.marieteamclient;

import java.io.IOException;

import fr.marieteamclient.constants.Constants;
import fr.marieteamclient.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Classe principale de l'application MarieTeam.
 * Initialise et lance l'interface graphique de l'application.
 * Gère la connexion à la base de données et la configuration de la fenêtre principale.
 */
public class GeneratorApplication extends Application {
    
    /**
     * Méthode principale de démarrage de l'application JavaFX.
     * Configure la fenêtre principale, teste la connexion à la base de données
     * et initialise l'interface utilisateur.
     *
     * @param stage La fenêtre principale de l'application
     * @throws IOException Si une erreur survient lors du chargement de l'interface
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Tester la connexion à la base de données
        try {
            DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
            database.getConnection();
            System.out.println("Connexion à la base de données réussie !");
            database.closeConnection();
        } catch (RuntimeException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(GeneratorApplication.class.getResource("home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Constants.APP_WIDTH, Constants.APP_HEIGHT);
        
        // Passer la référence de la scène au contrôleur
        HomeController controller = fxmlLoader.getController();
        controller.setStage(stage);
        
        stage.setTitle(Constants.APP_NAME);
        stage.setResizable(true);
        //stage.setMaximized(true);
        stage.setScene(scene);
        stage.getIcons().add(new Image(GeneratorApplication.class.getResourceAsStream("/fr/marieteamclient/logo_app_mt.png")));
        stage.show();
    }

    /**
     * Point d'entrée de l'application.
     * Lance l'application JavaFX.
     *
     * @param args Les arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch();
    }
}
