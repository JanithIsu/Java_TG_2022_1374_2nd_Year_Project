package Database;

import java.sql.*;


public class dbconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/javaminiproject";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection(){
        try {
            //Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
