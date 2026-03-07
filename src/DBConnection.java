package src;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    static String url = "jdbc:mysql://localhost:3306/vehicle_rental";
    static String username = "root";
    static String password = "root123";

    public static Connection getConnection() {

        try {

            Connection con = DriverManager.getConnection(url, username, password);

            return con;

        } catch (Exception e) {

            System.out.println("Database connection failed");
            e.printStackTrace();
            return null;
        }
    }
}