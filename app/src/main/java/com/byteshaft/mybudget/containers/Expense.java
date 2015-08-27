package com.byteshaft.mybudget.containers;
/*
    Holds info about an expense for a line item. Used by ItemHistoryAdapter
 */
public class Expense {

    private String name;
    private String date;
    private float amount;

    public Expense(String name, String date, float amount) {
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }

}
