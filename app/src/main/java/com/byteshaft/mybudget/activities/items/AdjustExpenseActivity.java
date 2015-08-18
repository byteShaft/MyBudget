package com.byteshaft.mybudget.activities.items;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.database.DBHelper;

/*
    Dialog to allow user to adjust an expense's amount and/or name
 */
public class AdjustExpenseActivity extends AppCompatActivity {

    private String expenseName;
    private String expenseDate;
    private String itemName;
    private int expenseAmount;
    private int remaining;

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_expense);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDb = DBHelper.getInstance(this);

        Bundle b = getIntent().getExtras();
        expenseName = b.getString("EXPENSE_NAME");
        expenseDate = b.getString("EXPENSE_DATE");
        expenseAmount = b.getInt("EXPENSE_AMOUNT");
        itemName = b.getString("ITEM_NAME");
        remaining = b.getInt("ITEM_REMAINING");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onSubmitClick(View v) {

        EditText newNameView = (EditText) findViewById(R.id.name);
        EditText newAmountView = (EditText) findViewById(R.id.amount);

        String newName = newNameView.getText().toString();
        String newAmountStr = newAmountView.getText().toString();

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        // validation
        if(newName.equals("") && newAmountStr.equals("")) {

            text = "Both a name and an amount have to be specified, please try again";
            Toast.makeText(context, text, duration).show();

        } else if(!newName.equals("") && newAmountStr.equals("")) {

            if(!(newName.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {
                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();
            } else {
                myDb.updateExpense(itemName, expenseName, newName, expenseDate, expenseAmount);
                text = "Expense has been adjusted";
                Toast.makeText(context, text, duration).show();

                finish();
            }

        } else {

            int newAmount = Integer.parseInt(newAmountStr);

            if(newAmount > (remaining + expenseAmount)) {   // calculates what the remaining would be minus this item

                text = "Amount exceeds remaining budget for that item, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                if(newName.equals("")) {
                    myDb.updateExpense(itemName, expenseName, expenseName, expenseDate, newAmount);

                    text = "Expense has been adjusted";
                    Toast.makeText(context, text, duration).show();

                    finish();
                }
                else {

                    if(!(newName.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {
                        text = "Name can only contain letters, please try again";
                        Toast.makeText(context, text, duration).show();
                    } else {
                        myDb.updateExpense(itemName, expenseName, newName, expenseDate, newAmount);
                        text = "Expense has been adjusted";
                        Toast.makeText(context, text, duration).show();

                        finish();
                    }


                }

            }

        }

    }

    public void onDeleteExpenseClick(View v) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AdjustExpenseActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Context context = getApplicationContext();
                CharSequence text = "Item deleted";
                int duration = Toast.LENGTH_SHORT;

                myDb.deleteExpense(itemName, expenseName);

                Toast.makeText(context, text, duration).show();
                finish();

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete this expense?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();


    }

}
