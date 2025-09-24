package com.expense.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;




public class DatabaseConnection {
    public static final String URL = "jdbc:mysql://localhost:3306/income";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "Kit23@12345";

    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e){
            System.out.println("JDBC Driver is missing");
            System.exit(1);
        }
    }

    public static Connection getDBConnection() throws SQLException{
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }


}
