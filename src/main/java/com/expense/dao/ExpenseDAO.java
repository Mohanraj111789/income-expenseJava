package com.expense.dao;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.Statement;

import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.util.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
public class ExpenseDAO {
    private static final String SELECT_ALL = "SELECT * FROM category";
    private static final String SELECT_EXP = "SELECT * FROM expense";

    private Category getCategoryRow(ResultSet rs) throws SQLException{
        int id = rs.getInt("category_id");
        String catname = rs.getString("category_name");
        Category cat = new Category(id, catname);
        return cat;
    }

    private Expense getCategoryRow1(ResultSet rs) throws SQLException
    {
        int id = rs.getInt("expense_id");
        String name = rs.getString("expense_name");
        double amount = rs.getDouble("expense_amount");
        int categoryId = rs.getInt("category_id");
        Timestamp timestamp = rs.getTimestamp("expense_date");
        Expense exp = new Expense(id, name, amount, categoryId, timestamp);
        return exp;
    }

    public List<Category> getAllcat() throws SQLException{
        List<Category> cats = new ArrayList<>();
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_ALL)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    cats.add(getCategoryRow(rs));
                }
            }
        return cats;
    }

    public List<Expense> getAllExpenses() throws SQLException
    {
        List<Expense> exps = new ArrayList<>();
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_EXP)) {
                ResultSet rs = stmt.executeQuery();
                while(rs.next()){
                    exps.add(getCategoryRow1(rs));
                }
            }
        return exps;
    }
    
}
