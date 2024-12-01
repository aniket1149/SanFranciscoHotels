package server.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/travel_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root"; // Replace with your MySQL username
    private static final String PASSWORD = "1234@Aniket"; // Replace with your MySQL password
    //TO_DO Configure Application Properties.
    public static Connection getConnection() throws SQLException {
       try{
           return DriverManager.getConnection(URL, USERNAME, PASSWORD);
       }catch (SQLException e){
           e.printStackTrace();
       }
        return null;
    }

}
