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
        // Tester la connexion à la base de données
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Connexion à la base de données réussie !");
            conn.close();
        } catch (SQLException e) {
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

    public static void main(String[] args) {
        launch();
    }
}
