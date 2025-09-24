package com.expense.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.expense.model.Category;
import com.expense.model.Expense;
import com.expense.dao.ExpenseDAO;
import java.time.LocalDateTime;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

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
        category.setPreferredSize(new Dimension(500,500));
        category.setBackground(Color.GREEN);
        category.setFont(new Font("Arial",Font.BOLD,50));
        expense.setPreferredSize(new Dimension(500,500));
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
    }

    private void intializeComponents(){
        setTitle("Expense Tracker");
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
}

class ExpenseUI extends JFrame {
    private ExpenseDAO expenseDAO;
    private JTable expenseTable;
    private DefaultTableModel expenseTableModel;

    public ExpenseUI() {
        this.expenseDAO = new ExpenseDAO();
        intializeComponents();
        setupLayout();
        loadExpense();
    }

    private void intializeComponents()
    {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);
        String[] columns = {"ID","Name","Amount","Category","Date"};
        expenseTableModel = new DefaultTableModel(columns,0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        expenseTable = new JTable(expenseTableModel);
    }
    
    private void setupLayout() {
  
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
            expenseTableModel.addRow(new Object[]{
                expense.getId(),
                expense.getName(),
                expense.getAmount(),
                expense.getCategoryId(),
                expense.getDate()
            });
        }
    }
}

