package com.byteshaft.mybudget.activities.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Utils.Helpers;
import com.byteshaft.mybudget.containers.LineItem;
import com.byteshaft.mybudget.database.DBHelper;

/*
    A dialog-like activity that prompts a user to add a new expense for a line item.

    Started by ItemHistoryActivity.
 */

public class AddExpenseActivity extends AppCompatActivity {

    private String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            itemName = b.getString("ITEM_NAME");
            getSupportActionBar().setTitle(itemName + ": Add Expense");
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Item name was not provided";
            int duration = Toast.LENGTH_SHORT;
            Toast.makeText(context, text, duration).show();
            finish();
        }
    }

    public void onAddExpenseClick(View v) {
        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;
        EditText descHolder = (EditText) findViewById(R.id.desc);
        EditText amountHolder = (EditText) findViewById(R.id.amount);
        String desc = descHolder.getText().toString();
        String amountStr = amountHolder.getText().toString();

        // get current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        String date = formatter.format(c.getTime());
        DBHelper myDb;
        // get item
        if (AppGlobals.getsCurrentMonthYear() != null) {
            myDb = new DBHelper(getApplicationContext(), AppGlobals.getsCurrentMonthYear()+".db");
        } else {
            myDb = new DBHelper(getApplicationContext(), Helpers.getTimeStamp("MMM_yyyy")+".db");
        }

        LineItem item = myDb.getLineItem(itemName);
        if (desc.equals("") || amountStr.equals("")) {
            text = "Both a description and an amount must be entered, please try again!";
            Toast.makeText(context, text, duration).show();
        } else {
            int amount = Integer.parseInt(amountStr);
            if (!(desc.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {
                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();
            } else if (Integer.parseInt(amountStr) > item.getRemaining()) {
                text = "Expense exceeds remaining budget for that item, please try again!";
                Toast.makeText(context, text, duration).show();
            } else {

                boolean result = myDb.addExpense(itemName, date, desc, amount);
                if (result) {
                    updateBudget(amount);
                    text = "Expense added!";
                    Toast.makeText(context, text, duration).show();
                    finish();
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // convenience method to handle SharedPreferences updating of "spent" value
    public void updateBudget(int spent) {
        SharedPreferences preferences = getSharedPreferences(HomeFragment.PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("curSpent", preferences.getInt("curSpent", 0) + spent);
        editor.commit();
    }
}
