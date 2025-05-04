package fr.marieteamclient.database;

import fr.marieteamclient.constants.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {
  private DatabaseConnection database = new DatabaseConnection(Constants.DATABASE_URL, Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
  private Connection connection = database.getConnection();

  @Test
  @DisplayName("1. Test de l'établissement de la connexion")
  void testGetConnection() {
    if (connection != null) {
      assertEquals(connection, database.getConnection());
    } else {
      fail("Connection to database not obtained. Please check the database connection.");
    }
  }

  @Test
  @DisplayName("2. Test de connexion avec identifiants invalides")
  void testInvalidDatabaseConnection() {
    DatabaseConnection connection = new DatabaseConnection("invalid_url", "invalid_user", "invalid_password");
    assertThrows(RuntimeException.class, () -> connection.getConnection());
  }
}