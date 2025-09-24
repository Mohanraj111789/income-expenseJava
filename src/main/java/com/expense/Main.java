package com.expense;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.expense.gui.MainUI;
import com.expense.util.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection db_connection = new DatabaseConnection();
        try{
            Connection cn = db_connection.getDBConnection();
            System.out.println("Database connection has been established");
        }
        catch(SQLException e){
            System.out.println("Database connection has failed");
        }

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e){
            System.err.println("Failed to set Look and Feel"+e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            try{
                new MainUI().setVisible(true);
            }
            catch(Exception e){
                System.err.println("Failed to open the GUI" +e.getMessage());
            }
        });
    }
}