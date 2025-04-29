package fr.marieteamclient.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/marieteam";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String testConnection() {
        try (Connection conn = getConnection()) {
            return "Connexion à la base de données réussie !";
        } catch (SQLException e) {
            return "Erreur de connexion à la base de données : " + e.getMessage();
        }
    }

    // Méthode principale pour tester
    public static void main(String[] args) {
        System.out.println(testConnection());  // Affiche le résultat dans la console
    }
}
