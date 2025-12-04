package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_splitter";
    private static final String USER = "root";
    private static final String PASSWORD = "L0r1@20803";

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
