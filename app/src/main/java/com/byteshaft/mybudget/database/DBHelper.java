package com.byteshaft.mybudget.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.containers.Expense;
import com.byteshaft.mybudget.containers.Goal;
import com.byteshaft.mybudget.containers.LineItem;

public class DBHelper extends SQLiteOpenHelper {

    private static final String BUDGET_TABLE_NAME = "budget";
    private static final String BUDGET_ITEM_ID = "id";
    private static final String BUDGET_ITEM_NAME = "name";
    private static final String BUDGET_ITEM_BUDGETED = "budgeted";
    private static final String BUDGET_ITEM_SPENT = "spent";
    private static final String BUDGET_ITEM_REMAINING = "remaining";

    /*****************************************************************
     * OVERALL DATABASE FUNCTIONS
     * Handles functionality to manage the database as a whole
     *****************************************************************/

    public DBHelper(Context context, String databaseName) {
        super(context, databaseName, null, 1);
        System.out.println(databaseName);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table  " + BUDGET_TABLE_NAME + " " +
                        "(id integer primary key, name text,budgeted integer, spent integer, remaining integer)"
        );
        db.execSQL(
                "create table goals " +
                        "(id integer primary key, name text, goal integer, deposited integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BUDGET_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS goals");
        onCreate(db);
    }

    /*****************************************************************
     * BUDGET FUNCTIONS
     * Handles functionality to manage the user's budget
     *****************************************************************/

    public int getTotalAllocated() {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select budgeted from " + BUDGET_TABLE_NAME, null);

        int totalAllocated = 0;

        res.moveToFirst();

        while (!res.isAfterLast()) {

            totalAllocated += res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED));
            res.moveToNext();

        }
        return totalAllocated;
    }

    public int getTotalSpent() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + BUDGET_TABLE_NAME, null);

        int totalSpent = 0;

        res.moveToFirst();

        while (!res.isAfterLast()) {

            totalSpent += res.getInt(res.getColumnIndex(BUDGET_ITEM_SPENT));
            res.moveToNext();

        }
        res.close();
        return totalSpent;

    }

    public int getNoRows() {
        SQLiteDatabase myDb = this.getWritableDatabase();
        int value =  (int) DatabaseUtils.queryNumEntries(myDb, BUDGET_TABLE_NAME);
        return value;
    }

    public void clearBudget() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM " + BUDGET_TABLE_NAME, null);
        try {
            res.moveToFirst();

            while (!res.isAfterLast()) {
                myDb.execSQL("DROP TABLE " + getTableName(res.getString(res.getColumnIndex("name"))));
                res.moveToNext();
            }

            myDb.execSQL("DROP TABLE " + BUDGET_TABLE_NAME);
        } catch (SQLiteException e) {
            Log.i(AppGlobals.getLogTag(getClass()), "Table already deleted");
        }
        res.close();
    }

    public void checkBudgetIsDefined() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("CREATE TABLE IF NOT EXISTS " + BUDGET_TABLE_NAME + " " + "(id integer primary key, name text,budgeted integer, spent integer, remaining integer)");

    }

    /*****************************************************************
     * LINE ITEM FUNCTIONS
     * Manages creation, deletion and manipulation of line items
     *****************************************************************/

    public boolean checkNameExists(String name) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor c = myDb.rawQuery("select * from " + BUDGET_TABLE_NAME + " where name='" + name + "'", null);

        if (c.getCount() > 0) {
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    public boolean insertLineItem(String name, int budgeted, int spent) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        int remaining = budgeted - spent;

        cv.put("name", name);
        cv.put("budgeted", budgeted);
        cv.put("spent", spent);
        cv.put("remaining", remaining);
        myDb.insert(BUDGET_TABLE_NAME, null, cv);

        myDb.execSQL(
                "create table " + getTableName(name) + " " +
                        "(id integer primary key, name text, date text, amount integer)"
        );
        return true;
    }

    public LineItem getLineItem(String name) {

        LineItem item = null;

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor c = myDb.rawQuery("select * from " + BUDGET_TABLE_NAME + " where name='" + name + "'", null);

        c.moveToFirst();

        while (!c.isAfterLast()) {

            item = new LineItem(
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_ID)),
                    c.getString(c.getColumnIndex(BUDGET_ITEM_NAME)),
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_BUDGETED)),
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_SPENT)),
                    c.getInt(c.getColumnIndex(BUDGET_ITEM_REMAINING))
            );

            c.moveToNext();
        }
        c.close();
        return item;

    }

    public ArrayList getAllLineItems() {
        ArrayList lineItems = new ArrayList<>();
        LineItem curItem;
        SQLiteDatabase myDb = this.getReadableDatabase();
        Cursor res = myDb.rawQuery("select * from " + BUDGET_TABLE_NAME, null);
        while (res.moveToNext()) {
            curItem = new LineItem(
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)),
                    res.getString(res.getColumnIndex(BUDGET_ITEM_NAME)),
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED)),
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_SPENT)),
                    res.getInt(res.getColumnIndex(BUDGET_ITEM_REMAINING))
            );

            lineItems.add(curItem);
        }
        res.close();
        return lineItems;

    }

    public int getItemSpent(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + getTableName(name), null);

        int spent = 0;

        res.moveToFirst();

        while (!res.isAfterLast()) {
            spent += res.getInt(res.getColumnIndex("amount"));

            res.moveToNext();
        }
        res.close();
        return spent;
    }

    public void updateItemState(String tableName, String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from " + BUDGET_TABLE_NAME + " where name='" + name + "'", null);

        res.moveToFirst();

        int budget = res.getInt(res.getColumnIndex(BUDGET_ITEM_BUDGETED));
        int spent = getItemSpent(tableName);
        int remaining = budget - spent;

        cv.put("name", name);
        cv.put("budgeted", budget);
        cv.put("spent", spent);
        cv.put("remaining", remaining);

        myDb.update(BUDGET_TABLE_NAME, cv, "id = ?", new String[]{
                Integer.toString(res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)))
        });
        res.close();

    }

    public void updateItem(String oldName, String newName, int newBudget, int spent) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from " + BUDGET_TABLE_NAME + " where name='" + oldName + "'", null);
        res.moveToFirst();

        cv.put("name", newName);
        cv.put("budgeted", newBudget);
        cv.put("spent", spent);
        cv.put("remaining", (newBudget - spent));

        myDb.update(BUDGET_TABLE_NAME, cv, "id = ?", new String[]{
                Integer.toString(res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)))
        });

        newName = getTableName(newName);
        oldName = getTableName(oldName);

        if (!oldName.equals(newName))
            myDb.execSQL("ALTER TABLE " + oldName + " RENAME TO " + newName);
        res.close();
    }

    public void deleteLineItem(String itemName) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("DELETE FROM " + BUDGET_TABLE_NAME + " WHERE NAME ='" + itemName + "'");

        // cycle through goals and check if they contain deposits under the itemName, then delete
        ArrayList goals = getGoalNames();
        Cursor res = null;
        String goalName;

        // if any deposits exist for the item, delete them from the respective goal/deposit table
        for (int i = 0; i < goals.size(); i++) {

            goalName = getGoalTableName((String) goals.get(i));

            res = myDb.rawQuery("SELECT * FROM " + goalName + " WHERE NAME = '" + itemName + "'", null);

            if (res.getCount() > 0) {

                myDb.execSQL("DELETE FROM " + goalName + " WHERE NAME = '" + itemName + "'");
                updateGoalState((String) goals.get(i));

            }
        }
        myDb.execSQL("DROP TABLE " + getTableName(itemName));
        res.close();
    }

    /*****************************************************************
     * EXPENSE FUNCTIONS
     * Manages creation, deletion and manipulation of expenses
     *****************************************************************/

    public boolean addExpense(String name, String date, String desc, int amount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", desc);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.insert(getTableName(name), null, cv);
        updateItemState(getTableName(name), name);
        return true;
    }

    public void updateExpense(String itemName, String oldName, String newName, String date, int newAmount) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String tableName = getTableName(itemName);
        Cursor res = myDb.rawQuery("select * from " + tableName + " where name='" + oldName + "'", null);

        res.moveToFirst();

        cv.put("name", newName);
        cv.put("date", date);
        cv.put("amount", newAmount);

        myDb.update(tableName, cv, "id = ?", new String[]{
                Integer.toString(res.getInt(res.getColumnIndex(BUDGET_ITEM_ID)))
        });

        updateItemState(tableName, itemName);
        res.close();
    }

    /*
        Creates an ArrayList of Expense objects for ItemHistoryActivity to parse and display
     */
    public ArrayList getExpenseHistory(String name) {
        ArrayList history = new ArrayList<>();
        Expense cur;
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from " + getTableName(name), null);

        if (res.getCount() <= 0) {
            return history;
        }

        res.moveToFirst();

        while (!res.isAfterLast()) {

            cur = new Expense(
                    res.getString(res.getColumnIndex("name")),
                    res.getString(res.getColumnIndex("date")),
                    res.getInt(res.getColumnIndex("amount"))
            );

            history.add(cur);
            res.moveToNext();
        }
        res.close();
        return history;
    }

    public void deleteExpense(String itemName, String expenseName) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        String tableName = getTableName(itemName);

        myDb.execSQL("DELETE FROM " + tableName + " WHERE NAME ='" + expenseName + "'");

        updateItemState(tableName, itemName);
    }

    /*****************************************************************
     * GOAL FUNCTIONS
     * Manages creation, adjustment and deletion of goals
     *****************************************************************/

    public boolean checkGoalExists(String name) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS WHERE NAME='" + name + "'", null);

        if (res.getCount() > 0) {
            res.close();
            return true;
        } else {
            res.close();
            return false;
        }

    }

    public boolean addGoal(String name, int goalAmount) {
        SQLiteDatabase myDb = getWritableDatabase();
        if (checkGoalExists(name)) {
            return false;
        } else {
            myDb = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("name", name);
            cv.put("goal", goalAmount);
            cv.put("deposited", 0);

            myDb.insert("goals", null, cv);

            myDb.execSQL(
                    "create table " + getGoalTableName(name) + " " +
                            "(id integer primary key, name text, amount integer, date text)"
            );
            return true;
        }
    }

    public void adjustGoal(String oldName, String newName, int newGoal) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("select * from goals where name='" + oldName + "'", null);

        res.moveToFirst();

        cv.put("name", newName);
        cv.put("goal", newGoal);

        myDb.update("goals", cv, "id = ?", new String[]{
                Integer.toString(res.getInt(res.getColumnIndex("id")))
        });

        if (!oldName.equals(newName))
            myDb.execSQL("ALTER TABLE " + getGoalTableName(oldName) + " RENAME TO " + getGoalTableName(newName));
        res.close();
    }

    public void updateGoalState(String goalName) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS WHERE NAME='" + goalName + "'", null);
        Cursor depositCur = myDb.rawQuery("SELECT * FROM " + getGoalTableName(goalName), null);

        int deposited = 0;

        depositCur.moveToFirst();

        while (!depositCur.isAfterLast()) {

            deposited = deposited + (depositCur.getInt(depositCur.getColumnIndex("amount")));

            depositCur.moveToNext();
        }
        res.moveToFirst();

        cv.put("name", goalName);
        cv.put("goal", res.getInt(res.getColumnIndex("goal")));
        cv.put("deposited", deposited);

        myDb.update("goals", cv, "id = ?", new String[]{
                Integer.toString(res.getInt(res.getColumnIndex("id")))
        });
        res.close();
    }

    public void deleteGoal(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();

        myDb.execSQL("DELETE FROM GOALS WHERE NAME ='" + name + "'");
        myDb.execSQL("DROP TABLE " + getGoalTableName(name));
    }

    public ArrayList getAllGoals() {
        ArrayList goals = new ArrayList<Goal>();
        Goal curGoal;

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from goals", null);

        res.moveToFirst();

        while (!res.isAfterLast()) {

            curGoal = new Goal(
                    res.getString(res.getColumnIndex("name")),
                    res.getInt(res.getColumnIndex("goal")),
                    res.getInt(res.getColumnIndex("deposited"))
            );

            goals.add(curGoal);

            res.moveToNext();

        }
        res.close();
        return goals;
    }

    // Used to create ArrayAdapter for MakeDepositActivity's Spinner
    public ArrayList<String> getGoalNames() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from goals", null);
        ArrayList<String> goalNames = new ArrayList<String>();

        res.moveToFirst();

        while (!res.isAfterLast()) {

            goalNames.add(res.getString(res.getColumnIndex("name")));

            res.moveToNext();

        }
        res.close();
        return goalNames;

    }

    public Goal getGoal(String name) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("select * from goals where name='" + name + "'", null);

        res.moveToFirst();
        return new Goal(
                res.getString(res.getColumnIndex("name")),
                res.getInt(res.getColumnIndex("goal")),
                res.getInt(res.getColumnIndex("deposited"))
        );
    }

    public void deleteGoals() {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS", null);

        res.moveToFirst();

        while (!res.isAfterLast()) {

            myDb.execSQL("DROP TABLE " + getGoalTableName(res.getString(res.getColumnIndex("name"))));

            res.moveToNext();

        }
        myDb.execSQL("DROP TABLE GOALS");
        res.close();
    }

    public void checkGoalTableIsDefined() {

        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("CREATE TABLE IF NOT EXISTS GOALS " + "(id integer primary key, name text, goal integer, deposited integer)");
    }

    public int getGoalRemaining(String goalName) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM GOALS WHERE NAME='" + goalName + "'", null);

        res.moveToFirst();

        int remaining = res.getInt(res.getColumnIndex("goal")) - res.getInt(res.getColumnIndex("deposited"));
        res.close();
        return remaining;

    }

    /*****************************************************************
     * DEPOSIT FUNCTIONS
     * Manages creation, adjustment and deletion of goal deposits
     *****************************************************************/

    public void addDeposit(String goalName, String depositName, int amount, boolean isFromExpense) {

        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        String date = formatter.format(c.getTime());

        cv.put("name", depositName);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.insert(getGoalTableName(goalName), null, cv);

        if (isFromExpense)
            addExpense(depositName, date, "Deposit: " + goalName, amount);
        updateGoalState(goalName);
    }

    public void adjustDeposit(String goalName, String depositName, String date, int amount) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String tableName = getGoalTableName(goalName);
        Cursor res = myDb.rawQuery("SELECT * FROM " + tableName + " WHERE NAME ='" + depositName + "'", null);
        res.moveToFirst();
        cv.put("name", depositName);
        cv.put("date", date);
        cv.put("amount", amount);

        myDb.update(tableName, cv, "id = ?", new String[]{
                Integer.toString(res.getInt(res.getColumnIndex("id")))
        });
        updateGoalState(goalName);
    }

    public void deleteDeposit(String goalName, String depositName, String expenseName) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        myDb.execSQL("DELETE FROM " + getGoalTableName(goalName) + " WHERE NAME ='" + depositName + "'");
        deleteExpense(depositName, expenseName);
        updateGoalState(goalName);
    }

    public ArrayList getDepositHistory(String goalName) {
        SQLiteDatabase myDb = this.getWritableDatabase();
        Cursor res = myDb.rawQuery("SELECT * FROM " + getGoalTableName(goalName), null);
        ArrayList depositHistory = new ArrayList<Expense>();
        Expense curDeposit;

        res.moveToFirst();

        while (!res.isAfterLast()) {

            curDeposit = new Expense(
                    res.getString(res.getColumnIndex("name")),
                    res.getString(res.getColumnIndex("date")),
                    res.getInt(res.getColumnIndex("amount"))
            );

            depositHistory.add(curDeposit);
            res.moveToNext();
        }
        res.close();
        return depositHistory;
    }

    /*****************************************************************
     * CONVENIENCE METHODS
     *****************************************************************/

    public String getTableName(String name) {
        String tableName = name.replaceAll("\\s+", "");
        tableName = tableName.toLowerCase();
        return tableName;
    }

    public String getGoalTableName(String name) {
        String tableName = name.replaceAll("\\s+", "");
        tableName = tableName.toLowerCase();
        tableName = "goal" + tableName;
        return tableName;
    }
}
