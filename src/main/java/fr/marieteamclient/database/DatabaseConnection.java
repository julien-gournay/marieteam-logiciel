package fr.marieteamclient.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import fr.marieteamclient.constants.Constants;

public class DatabaseConnection {

    // Connexion à la base de données
    public static Connection connect() throws SQLException {
        try {
            // Charger le driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found", e);
        }

        // Connexion à la base de données
        return DriverManager.getConnection(
                Constants.DATABASE_URL,
                Constants.DATABASE_USER,
                Constants.DATABASE_PASSWORD
        );
    }

    // Méthode pour tester la connexion à la base de données
    public static String testConnection() {
        try (Connection conn = connect()) {
            if (conn != null) {
                return "Connexion réussie à la base de données.";
            } else {
                return "La connexion a échoué.";
            }
        } catch (SQLException e) {
            return "Erreur de connexion à la base de données : " + e.getMessage();
        }
    }

    // Méthode principale pour tester
    public static void main(String[] args) {
        System.out.println(testConnection());  // Affiche le résultat dans la console
    }
}
