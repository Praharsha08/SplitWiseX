package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();

        try(InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null) {
                throw new RuntimeException("Sorry, unable to find config.properties");
            }

            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database configuration", e);
        }
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}
