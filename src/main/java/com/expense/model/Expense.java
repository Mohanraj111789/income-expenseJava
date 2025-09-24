package com.expense.model;

import java.util.Date;

public class Expense {
    private int id;
    private String name;
    private double amount;
    private int categoryId;
    private Date date;

    public Expense(int id, String name, double amount, int categoryId, Date date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.categoryId = categoryId;
        this.date = date;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public Date getDate() {
        return date;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
}
