package com.expense.model;

import java.util.Date;

public class Expense {
    private String name;
    private double amount;
    private int id;
    private int category_id;
    private Date transaction_date;
    private String category_name;

    private String description;
    private Date date;

    public Expense(int id,String name, double amount, String description, Date date, String category_name) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category_name = category_name;
    }
    public Expense(String name, double amount, String description, Date date, String category_name) {
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category_name = category_name;
    }
    public Expense(int id,String name, double amount, String description, Date date, int category_id) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category_id = category_id;
    }
    public Expense(String name, double amount, String description, Date date, int category_id) {
        this.name = name;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }
    public Date getDate() {
        return date;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCategory_id() {
        return category_id;
    }
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
    public Date getTransaction_date() {
        return transaction_date;
    }
    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }
    public String getCategory_name() {
        return category_name;
    }
    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
