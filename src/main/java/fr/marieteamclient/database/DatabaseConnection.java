package fr.marieteamclient.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe gérant la connexion à la base de données.
 * Fournit des méthodes pour établir et tester la connexion à la base de données MySQL.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/marieteam";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Établit une connexion à la base de données.
     *
     * @return Une connexion à la base de données
     * @throws SQLException Si une erreur survient lors de la connexion
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Teste la connexion à la base de données.
     *
     * @return Un message indiquant le succès ou l'échec de la connexion
     */
    public static String testConnection() {
        try (Connection conn = getConnection()) {
            return "Connexion à la base de données réussie !";
        } catch (SQLException e) {
            return "Erreur de connexion à la base de données : " + e.getMessage();
        }
    }

    /**
     * Méthode principale pour tester la connexion à la base de données.
     * Affiche le résultat du test dans la console.
     *
     * @param args Les arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        System.out.println(testConnection());  // Affiche le résultat dans la console
    }
}
