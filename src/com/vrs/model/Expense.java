package com.vrs.model;

public class Expense {
    private int expenseId;
    private int vehicleId;
    private String description;
    private double amount;
    private String expenseDate;

    public Expense(int expenseId, int vehicleId, String description,
                   double amount, String expenseDate) {
        this.expenseId = expenseId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getExpenseDate() {
        return expenseDate;
    }
}