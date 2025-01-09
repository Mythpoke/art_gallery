package artGallery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/art_gallery";
    private static final String USER = "root";  // Replace with your MySQL username
    private static final String PASSWORD = ""; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
