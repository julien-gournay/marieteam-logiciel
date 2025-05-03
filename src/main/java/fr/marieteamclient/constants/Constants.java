package fr.marieteamclient.constants;

import java.io.FileInputStream;
import java.util.Properties;

public final class Constants {

    public static final String DATABASE_URL;
    public static final String DATABASE_USER;
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

    public static final String APP_NAME = "MarieTeam Client";
    public static final String APP_VERSION = "1.0";
    public static final double APP_WIDTH = 1200;
    public static final double APP_HEIGHT = 700;
}