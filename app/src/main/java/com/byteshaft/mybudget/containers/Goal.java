package com.byteshaft.mybudget.containers;

public class Goal {

    private String name;
    private float goal;
    private float deposited;

    public Goal(String name, float goal, float deposited) {
        this.name = name;
        this.goal = goal;
        this.deposited = deposited;
    }

    public String getName() {
        return name;
    }

    public float getGoal() {
        return goal;
    }

    public float getDeposited() {
        return deposited;
    }


}
