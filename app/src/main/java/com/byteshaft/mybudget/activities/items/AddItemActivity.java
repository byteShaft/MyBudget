package com.byteshaft.mybudget.activities.items;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Utils.Helpers;
import com.byteshaft.mybudget.database.DBHelper;

/*
    A dialog-like activity that prompts a user to enter an name and amount for
    a new line item.
 */

public class AddItemActivity extends AppCompatActivity {

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        setTitle(R.string.activity_title);
        if (AppGlobals.getsCurrentMonthYear() != null) {
            myDb = new DBHelper(getApplicationContext(), AppGlobals.getsCurrentMonthYear()+".db");
        } else {
            myDb = new DBHelper(getApplicationContext(), Helpers.getTimeStamp("MMM_yyyy")+".db");
        }
    }

    public void onClick(View v) {
        EditText nameView = (EditText) findViewById(R.id.name);
        EditText amountView = (EditText) findViewById(R.id.amount);
        String name = nameView.getText().toString();
        String amountStr = amountView.getText().toString();
        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;
        int allocated = myDb.getTotalAllocated();
        SharedPreferences preferences = getSharedPreferences(AppGlobals.PREFS_NAME, 0);
        int curBudget = preferences.getInt(Helpers.getTimeStamp("MMM_yyyy"), 0);

        // basic input validation
        if (amountStr.equals("") || name.equals("")) {

            text = "Invalid input, please try again!";
            Toast.makeText(context, text, duration).show();

        } else if (!(name.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {

            text = "Item name can only contain letters, please try again!";
            Toast.makeText(context, text, duration).show();

        } else {

            if (allocated == curBudget) {
                text = "Current budget has already been completely allocated";
                Toast.makeText(context, text, duration).show();
                finish();
            } else if ((allocated + Integer.parseInt(amountStr)) > curBudget) {
                text = "Amount exceeds remaining allocatable budget, please try again!";
                Toast.makeText(context, text, duration).show();
            } else {

                if (myDb.checkNameExists(name)) {
                    text = "Item with that name already exists, please try again!";
                    Toast.makeText(context, text, duration).show();
                } else {
                    myDb.insertLineItem(name, Integer.parseInt(amountStr), 0);

                    text = "Item added. €" + Integer.toString(curBudget - myDb.getTotalAllocated()) + ".00 remaining to be allocated.";
                    duration = Toast.LENGTH_LONG;
                    Toast.makeText(context, text, duration).show();
                    finish();
                }
            }
        }
    }
}
