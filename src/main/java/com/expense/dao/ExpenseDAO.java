package com.expense.dao;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.util.DatabaseConnection;

public class ExpenseDAO {
    private static final String SELECT_ALL = "SELECT * FROM category";
    private static final String SELECT_EXP = "SELECT * FROM expense";
    private static final String INSERT_CATEGORY = "INSERT INTO category (category_name) VALUES (?)";
    private static final String FILTER_NAMES = "SELECT category_name FROM category";
    private static final String INSERT_EXPENSE = "INSERT INTO expense (expense_name, amount, category_id, description, transaction_date,created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

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
        double amount = rs.getDouble("amount");
        String description = rs.getString("description");
        Timestamp timestamp = rs.getTimestamp("transaction_date");
        String category_name = getCategoryName(rs.getInt("category_id"));
        Expense exp = new Expense(id,name, amount, description, new Date(timestamp.getTime()), category_name);
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
    public void addExpense(Expense expense) throws SQLException
    {
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_EXPENSE)) {
                stmt.setString(1, expense.getName());
                stmt.setDouble(2, expense.getAmount());
                stmt.setInt(3, expense.getCategory_id());
                stmt.setString(4, expense.getDescription());
                stmt.setTimestamp(5, new Timestamp(expense.getDate().getTime()));
                int rowsAffected = stmt.executeUpdate();
                if(rowsAffected == 0)
                {
                    throw new SQLException("Creating expense failed, no rows affected.");
                }
            }
    }
    public void addCategory(String name) throws SQLException
    {
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement(INSERT_CATEGORY)) {
                stmt.setString(1, name);
                int rowsAffected = stmt.executeUpdate();
                if(rowsAffected == 0)
                {
                    throw new SQLException("Creating category failed, no rows affected.");
                }
            }
    }
    public String[] getAllcatnames(int type) throws SQLException{
        List<String> names = new ArrayList<>();
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement(FILTER_NAMES)) {
                ResultSet rs = stmt.executeQuery();
                if(type == 1){
                names.add("All");
                }
                while(rs.next()){
                    names.add(rs.getString("category_name"));
                }
            }
        return names.toArray(new String[0]);


    }
    public int getCategoryID(String name) throws SQLException{
        int id = -1;
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement("Select category_id from category where category_name = ?")) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    id = rs.getInt("category_id");
                } else {
                    // No matching category found; keep id = -1 or throw an exception
                    id = -1;
                }
            }
        return id;
        }
    public String getCategoryName(int id) throws SQLException{
        String name = "";
        try(
            Connection con = DatabaseConnection.getDBConnection();
            PreparedStatement stmt = con.prepareStatement("Select category_name from category where category_id = ?")) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    name = rs.getString("category_name");
                } else {
                    // No matching category found; keep name = "" or throw an exception
                    name = "";
                }
            }
        return name;
        }

    
}
