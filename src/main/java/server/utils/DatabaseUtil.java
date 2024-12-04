package server.utils;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseUtil {
    private Properties config; // a "map" of properties
    private String uri = null;
    private static Connection connection = null;
    private static DatabaseUtil dbHandler = new DatabaseUtil("database.properties");

    private DatabaseUtil(String propertiesFile){
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database") + "?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try{
            connection = getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static DatabaseUtil getInstance() {
        return dbHandler;
    }

    public void createTable(String tableName) {
                try{
                    String createTable = null;
                    Statement statement = connection.createStatement();
                    switch (tableName){
                        case "users":
                            createTable = PreparedStatements.CREATE_USER_TABLE;
                    }
                    if(createTable != null){
                        statement.execute(createTable);
                    }
                }catch(SQLException e){
                    throw  new RuntimeException(e);
                }
    }

    private Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        }

        catch (IOException e) {
            System.out.println(e);
        }

        return config;
    }

    public Connection getConnection() throws SQLException {
       try{
           return DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"));
       }catch (SQLException e){
           e.printStackTrace();
       }
        return null;
    }

}
