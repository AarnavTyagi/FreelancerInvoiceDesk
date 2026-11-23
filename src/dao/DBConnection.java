package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens connections to MySQL.
 * The url, user and password are read from db.properties in the project folder,
 * so the password is never written in the code or uploaded to GitHub.
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static Properties config;

    private DBConnection() { }

    public static Connection getConnection() throws SQLException {
        Properties p = loadConfig();
        return DriverManager.getConnection(p.getProperty("url"), p.getProperty("user"), p.getProperty("password"));
    }

    private static Properties loadConfig() throws SQLException {
        if (config == null) {
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
                p.load(in);
            } catch (IOException e) {
                throw new SQLException("Could not read " + CONFIG_FILE + " in the project folder.", e);
            }
            config = p;
        }
        return config;
    }
}
