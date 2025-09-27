package com.expense.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.dao.ExpenseDAO;
import java.awt.*;
import java.io.StringBufferInputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

public class MainUI extends JFrame{
    private JButton category;
    private JButton expense;

    public MainUI(){
        intializeComponents();
        setupLayout();
        setUpListeners();

    }
    private void setUpListeners(){
        category.addActionListener(e -> {
            CategoryUI categoryUI = new CategoryUI();
            categoryUI.setVisible(true);
        });
        expense.addActionListener(e -> {
            ExpenseUI expenseUI = new ExpenseUI();
            expenseUI.setVisible(true);
        });
    }
    private void intializeComponents(){
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);
        category = new JButton("Category");
        expense = new JButton("Expense");
    }
    private void setupLayout() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(category);
        buttonPanel.add(expense);
        category.setPreferredSize(new Dimension(500,200));
        category.setBackground(Color.GREEN);
        category.setFont(new Font("Arial",Font.BOLD,50));
        expense.setPreferredSize(new Dimension(500,200));
        expense.setBackground(Color.RED);
        expense.setFont(new Font("Arial",Font.BOLD,50));
    
        JPanel mainpanel = new JPanel(new GridBagLayout()); // centers child panel
        mainpanel.add(buttonPanel);
    
        add(mainpanel, BorderLayout.CENTER);
    }
    
}
class CategoryUI extends JFrame{

    private JButton addCategory;
    private JButton updateCategory;
    private JButton deleteCategory;
    private JTable categoryTable;
    private DefaultTableModel categoryTableModel;
    private JTextField categoryName;
    private ExpenseDAO expenseDAO;




    public CategoryUI(){
        this.expenseDAO = new ExpenseDAO();
        intializeComponents();
        setupLayout();
        loadCategory();
        setUpListeners();
    }

    private void intializeComponents(){
        setTitle("Category");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);
        addCategory = new JButton("Add Category");
        updateCategory = new JButton("Update Category");
        deleteCategory = new JButton("Delete Category");
        
        // categoryTableModel = new DefaultTableModel();
        categoryName = new JTextField(20);
        String[] columns = {"ID","Name","Expense Count"};
        categoryTableModel = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        categoryTable = new JTable(categoryTableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if(!e.getValueIsAdjusting()){
                loadSelectedRow();
            }
        });
        
    }
    private void setupLayout(){
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx  = 0;
        gbc.gridy = 0;

        gbc.anchor = GridBagConstraints.WEST;

        inputPanel.add(new JLabel("category name"),gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(categoryName,gbc);


        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel,BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.add(addCategory);
        buttonPanel.add(updateCategory);
        buttonPanel.add(deleteCategory);
        northPanel.add(buttonPanel,BorderLayout.SOUTH);

        add(northPanel,BorderLayout.NORTH);
        add(new JScrollPane(categoryTable),BorderLayout.CENTER);
        
    }

    private void loadCategory(){
        try{
            List<Category> categories =  expenseDAO.getAllcat();
            updateCategoryTable(categories);
        }
        catch(Exception e){
            e.printStackTrace(); // This will print the detailed SQL error to the console
            JOptionPane.showMessageDialog(this, "Failed to load categories: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
            
    private void updateCategoryTable(List<Category> categories) {
        categoryTableModel.setRowCount(0);
        for (Category c : categories) {
            categoryTableModel.addRow(new Object[]{c.getId(), c.getName(),getExpenseCount(c.getId())});
        }
    }
    private void setUpListeners(){
        addCategory.addActionListener(e->{
            addCategorySql();
        });
        updateCategory.addActionListener(e->{
            updateCategorySql();
        });

        deleteCategory.addActionListener(e->{
            deleteCategorySql();
        });
    }
    private void addCategorySql()
    {
        String name = categoryName.getText().trim();
        if(name.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Category name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            expenseDAO.addCategory(name);
            loadCategory();
            categoryName.setText("");
            JOptionPane.showMessageDialog(this, "Category added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add category: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadSelectedRow()
    {
        int selectedRow = categoryTable.getSelectedRow();
        if(selectedRow == -1)
        {
            return;
        }
        String name = (String)categoryTableModel.getValueAt(selectedRow, 1);
        categoryName.setText(name);
    }

    // update category

    private void updateCategorySql(){
        int row = categoryTable.getSelectedRow();
        if(row>=0)
        {
            try{
                int id = (int)categoryTable.getValueAt(row, 0);
                String catName = (String) categoryName.getText().trim();
                Category category = new Category(id, catName);
                if(expenseDAO.updateCategorySql(category))
                {
                    JOptionPane.showMessageDialog(this,"Update Sucessfull","Info",JOptionPane.INFORMATION_MESSAGE);
                    loadCategory();
                    categoryName.setText("");

                }
            }
            catch(SQLException e)
            {
                JOptionPane.showMessageDialog(this, "SQL Error","Error",JOptionPane.ERROR_MESSAGE);
            }
        }

        else{
            JOptionPane.showConfirmDialog(this, "Select row to update","Error",JOptionPane.ERROR_MESSAGE);
        }


    }
    // Delete Category

    private void deleteCategorySql()
    {
        int row = categoryTable.getSelectedRow();
        try{
            if(row >= 0)
            {
                int id = (int) categoryTable.getValueAt(row, 0);
                Category cat = new Category(id);
                expenseDAO.deleteCategorySql(cat);
                loadCategory();
                categoryName.setText("");

                JOptionPane.showMessageDialog(this,"Delete Sucessfull","Info",JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                JOptionPane.showConfirmDialog(this,"Select row to Delete","Error",JOptionPane.ERROR_MESSAGE);
            }
    }catch(SQLException e)
    {
        JOptionPane.showMessageDialog(this, e,"Error",JOptionPane.ERROR_MESSAGE);
    }

    }
    private int getExpenseCount(int categoryId)
    {
        try{
            return expenseDAO.getExpenseCount(categoryId);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return 0;
        }
    }
}

class ExpenseUI extends JFrame {
    private ExpenseDAO expenseDAO;
    private JTable expenseTable;
    private DefaultTableModel expenseTableModel;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> filterComboBox;
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JLabel totalAmountField;

    public ExpenseUI() {
        this.expenseDAO = new ExpenseDAO();
        intializeComponents();
        setupLayout();
        loadExpense();
        setUpListeners();
    }

    private void intializeComponents()
    {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);

        titleField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        filterComboBox = new JComboBox<>(filteroption(1));
        totalAmountField = new JLabel("Total Amount: "+totalAmount());      
        
        addButton = new JButton("Add Expense");
        updateButton = new JButton("Update Expense");
        deleteButton = new JButton("Delete Expense");
        amountField = new JTextField(20);
        categoryComboBox = new JComboBox<>(filteroption(0));
        
        String[] columns = {"ID","Name","Amount","Category","Description","Date"};
        expenseTableModel = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        expenseTable = new JTable(expenseTableModel);
        expenseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        expenseTable.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if(!e.getValueIsAdjusting()){
                loadSelectedRow();
            }
                
        });
    }
    //setuplayout
    private void setupLayout() {
        
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(new JLabel ("Expense Name"),gbc);
        gbc.gridx=1;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField,gbc);

        gbc.gridx=0;
        gbc.gridy=1;
        inputPanel.add(new JLabel("Description"),gbc);
        gbc.gridx=1;
        inputPanel.add(new JScrollPane(descriptionArea),gbc);
        
        gbc.gridx=1;
        gbc.gridy=2;
        JPanel filterPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Select Category:"));
        filterPanel.add(filterComboBox);

        gbc.gridx=0;
        gbc.gridy=3;
        inputPanel.add(new JLabel("Amount:"),gbc);
        gbc.gridx=1;
        inputPanel.add(amountField,gbc);

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);

        
        JPanel buttonPanel=new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        
        
        JPanel northPanel=new JPanel(new BorderLayout());
        northPanel.add(inputPanel,BorderLayout.CENTER);
        northPanel.add(buttonPanel,BorderLayout.SOUTH);
        northPanel.add(filterPanel,BorderLayout.NORTH);
        
        JPanel totalAmountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalAmountPanel.add(totalAmountField);
        
        add(northPanel,BorderLayout.NORTH);
        add(totalAmountPanel,BorderLayout.SOUTH);
        
        add(new JScrollPane(expenseTable),BorderLayout.CENTER);


    }
    
    private void loadExpense() {
        try{
            List<Expense> expenses = expenseDAO.getAllExpenses();
            updateExpenseTable(expenses);
            setDefault();
            totalAmountField.setText("Total Amount: "+totalAmount());
            filterComboBox.setSelectedIndex(0);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private void loadSelectedRow(){
        int row = expenseTable.getSelectedRow();
        if(row >=0){
            titleField.setText(expenseTableModel.getValueAt(row, 1).toString()!=""?expenseTableModel.getValueAt(row, 1).toString():"NULL");
            amountField.setText(expenseTableModel.getValueAt(row, 2).toString()!=""?expenseTableModel.getValueAt(row, 2).toString():"NULL");
            categoryComboBox.setSelectedItem(expenseTableModel.getValueAt(row, 3).toString());
            descriptionArea.setText(expenseTableModel.getValueAt(row, 4).toString());       
            
        }
    }
    private void updateExpenseTable(List<Expense> expenses) {
        expenseTableModel.setRowCount(0);
        for (Expense expense : expenses) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            expenseTableModel.addRow(new Object[]{
                expense.getId(),
                expense.getName(),
                expense.getAmount(),
                expense.getCategory_name(),
                expense.getDescription(),
                expense.getDate() != null ? sdf.format(expense.getDate()) : ""
            });
        }
    }
    private String[] filteroption(int type){
        try{
            String[] options = expenseDAO.getAllcatnames(type);
            return options;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new String[0];
    }
    private void setUpListeners(){
        addButton.addActionListener(e->{
            addExpenseSql();
        });
        updateButton.addActionListener(e->{
            updateExpenseSql();
        });
        deleteButton.addActionListener(e->{
            deleteExpenseSql();
        });
        filterComboBox.addActionListener(e->{
            filterExpense();
        });
    
    }
    private void addExpenseSql(){
        try{
        String name = titleField.getText().trim();
        double amount = Double.parseDouble(amountField.getText().trim());
        int categoryID = expenseDAO.getCategoryID((String)categoryComboBox.getSelectedItem());
        System.out.println(categoryID);
        String description = descriptionArea.getText().trim();
        Expense expense = new Expense(name, amount, description, new Date(), categoryID);
        expenseDAO.addExpense(expense);
            loadExpense();
            JOptionPane.showMessageDialog(this, "Expense added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateExpenseSql(){
        try{
            int row = expenseTable.getSelectedRow();
            if(row != -1)
            {
                int id =(int) expenseTable.getValueAt(row, 0);
                String name = titleField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                int categoryID = expenseDAO.getCategoryID((String)categoryComboBox.getSelectedItem());
                String description = descriptionArea.getText().trim();
                Expense expense = new Expense(id,name, amount, description, new Date(), categoryID);
                expenseDAO.updateExpenseSql(expense);
                JOptionPane.showMessageDialog(this,"Update Successfull","Sucess",JOptionPane.INFORMATION_MESSAGE);
                loadExpense();
            }
            else{
                JOptionPane.showMessageDialog(this,"Select row to update","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteExpenseSql(){
        try{
            int row = expenseTable.getSelectedRow();
            if(row != -1)
            {
                int id =(int) expenseTable.getValueAt(row, 0);
                expenseDAO.deleteExpenseSql(id);
                JOptionPane.showMessageDialog(this,"Delete Successfull","Sucess",JOptionPane.INFORMATION_MESSAGE);
                loadExpense();
                filterComboBox.setSelectedIndex(0);

            }
            else{
                JOptionPane.showMessageDialog(this,"Select row to delete","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        catch(SQLException e)
        {
            JOptionPane.showConfirmDialog(this, "SQL ERROR!!","Error",JOptionPane.ERROR_MESSAGE);
            return;
        }

    }
    private void filterExpense(){
        try{
            String selectedCategory = (String)filterComboBox.getSelectedItem();
            if(selectedCategory.equals("All")){
                loadExpense();
                return;
            }
            else{
                int categoryID = expenseDAO.getCategoryID(selectedCategory);
                List<Expense> expenses = expenseDAO.filterExpenseByCategory(categoryID);
                updateExpenseTable(expenses);
                totalAmountField.setText("Total Amount: "+totalAmount());
                setDefault();
            } 
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to filter expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void setDefault()
    {
        titleField.setText("");
        amountField.setText("");
        
        descriptionArea.setText("");
    }
    private String totalAmount()
    {
        try{
            String name = (String)filterComboBox.getSelectedItem();
            if(name.equals("All")){
                double total = expenseDAO.totalExpenses();
                return String.format("%.2f",total);
            }
            else{
                int categoryID = expenseDAO.getCategoryID(name);
                double total = expenseDAO.totalExpensesByCategory(categoryID);
                return String.format("%.2f",total);
            }
    }
    catch(Exception e){
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to filter expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return "";
}
    
}

