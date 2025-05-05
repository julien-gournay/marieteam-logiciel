package fr.marieteamclient.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gère la connexion à une base de données via JDBC.
 * <p>
 * Cette classe permet d'établir, de récupérer et de fermer une connexion à une base de données
 * à partir d'une URL, d'un nom d'utilisateur et d'un mot de passe.
 * </p>
 */
public class DatabaseConnection {

  private final Connection connection;

  /**
   * Crée une nouvelle instance de {@code DatabaseConnection} et établit une connexion à la base de données.
   *
   * @param databaseUrl URL de la base de données (ex: {@code jdbc:sqlite:ma_base.db})
   * @param user        Nom d'utilisateur pour la connexion
   * @param password    Mot de passe associé à l'utilisateur
   * @throws RuntimeException si la connexion à la base de données échoue
   */
  public DatabaseConnection(String databaseUrl, String user, String password) {
    try {
      this.connection = DriverManager.getConnection(databaseUrl, user, password);
      if (connection != null) {
        System.out.println("\n☑️ MarieTeam: connection established to the database \n");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Impossible d'établir une connexion à la base de données", e);
    }
  }

  /**
   * Retourne l'objet {@link Connection} associé à cette instance.
   *
   * @return la connexion active à la base de données
   */
  public Connection getConnection() {
    return connection;
  }

  /**
   * Ferme la connexion à la base de données.
   *
   * @throws RuntimeException si une erreur survient lors de la fermeture
   */
  public void closeConnection() {
    try {
      connection.close();
    } catch (SQLException e) {
      throw new RuntimeException("Impossible de fermer la connexion à la base de données", e);
    }
  }
}
