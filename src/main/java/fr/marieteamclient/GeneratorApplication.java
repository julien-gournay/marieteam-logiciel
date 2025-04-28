package fr.marieteamclient;

import fr.marieteamclient.constants.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import fr.marieteamclient.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

import java.io.IOException;

public class GeneratorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GeneratorApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), Constants.APP_WIDTH, Constants.APP_HEIGHT);
        stage.setTitle(Constants.APP_NAME);
        stage.setResizable(true);
        //stage.setMaximized(true);
        stage.setScene(scene);
        stage.getIcons().add(new Image(GeneratorApplication.class.getResourceAsStream("/fr/marieteamclient/logo_app_mt.png")));
        stage.show();
    }

    public static void main(String[] args) {
        launch();

        // Tenter de se connecter à la base de données
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn != null) {
                System.out.println("Connexion à la base de données réussie !");
                // Afficher un message ou continuer avec le reste de l'application
            }
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }
}
