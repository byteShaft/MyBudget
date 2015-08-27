package com.byteshaft.mybudget.containers;
/*
    Holds info about an item.
 */
public class LineItem {

    private float id, budgeted, spent, remaining;
    private String name;

    public LineItem(float id, String name, float budgeted, float spent, float remaining) {

        this.id = id;
        this.name = name;
        this.budgeted = budgeted;
        this.spent = spent;
        this.remaining = remaining;

    }

    public float getId() {
        return id;
    }

    public float getBudget() {
        return budgeted;
    }

    public float getSpent() {
        return spent;
    }

    public float getRemaining() {
        return remaining;
    }

    public String getName() {
        return name;
    }

}
