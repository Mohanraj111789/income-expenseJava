package com.expense.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.dao.ExpenseDAO;
import java.awt.*;
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
    private JTable categoryTable;
    private DefaultTableModel categoryTableModel;
    private JTextField categoryName;
    private DefaultTableModel expenseTableModel;
    private JTable expenseTable;
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
        
        // categoryTableModel = new DefaultTableModel();
        categoryName = new JTextField(20);
        String[] columns = {"ID","Name"};
        categoryTableModel = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        categoryTable = new JTable(categoryTableModel);
        
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
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(addCategory,gbc);


        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(inputPanel,BorderLayout.CENTER);

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
            categoryTableModel.addRow(new Object[]{c.getId(), c.getName()});
        }
    }
    private void setUpListeners(){
        addCategory.addActionListener(e->{
            addCategorySql();

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
        
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
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
        expenseTable.getSelectionModel().addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
                getSelectedRow();
            }
                
        });
    }
    private void getSelectedRow(){
        int row = expenseTable.getSelectedRow();
        if(row >=0){
            titleField.setText(expenseTableModel.getValueAt(row, 1).toString());
            amountField.setText(expenseTableModel.getValueAt(row, 2).toString());
            categoryComboBox.setSelectedItem(expenseTableModel.getValueAt(row, 3).toString());
            descriptionArea.setText(expenseTableModel.getValueAt(row, 4).toString());       
            
        }
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


        add(northPanel,BorderLayout.NORTH);

        add(new JScrollPane(expenseTable),BorderLayout.CENTER);
    }
    
    private void loadExpense() {
        try{
            List<Expense> expenses = expenseDAO.getAllExpenses();
            updateExpenseTable(expenses);
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load expenses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        categoryComboBox.addActionListener(e->{
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
            titleField.setText("");
            amountField.setText("");
            categoryComboBox.setSelectedIndex(0);
            descriptionArea.setText("");
            JOptionPane.showMessageDialog(this, "Expense added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add expense: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateExpenseSql(){}
    private void deleteExpenseSql(){}
    private void filterExpense(){}
    
}

