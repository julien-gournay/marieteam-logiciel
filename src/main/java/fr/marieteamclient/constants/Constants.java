package fr.marieteamclient.constants;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Classe contenant les constantes de l'application.
 * Gère les paramètres de configuration de l'application et de la base de données.
 */
public final class Constants {

    /** URL de connexion à la base de données */
    public static final String DATABASE_URL;
    /** Nom d'utilisateur pour la connexion à la base de données */
    public static final String DATABASE_USER;
    /** Mot de passe pour la connexion à la base de données */
    public static final String DATABASE_PASSWORD;

    static {
        Properties props = new Properties();
        try(FileInputStream input = new FileInputStream(".properties")) {
            props.load(input);
            DATABASE_URL = props.getProperty("database.url");
            DATABASE_USER = props.getProperty("database.user");
            DATABASE_PASSWORD = props.getProperty("database.password");
        } catch (Exception e) {
            throw new RuntimeException("Unable to load properties file");
        }
    }

    /** Nom de l'application */
    public static final String APP_NAME = "MarieTeam Client";
    /** Version de l'application */
    public static final String APP_VERSION = "1.0";
    /** Largeur par défaut de la fenêtre de l'application */
    public static final double APP_WIDTH = 1200;
    /** Hauteur par défaut de la fenêtre de l'application */
    public static final double APP_HEIGHT = 700;

    /** Intervalle de vérification de la connexion à la base de données (3 minutes) */
    public static final int DATABASE_CHECK_INTERVAL = 3 * 60 * 1000;
}