package com.byteshaft.mybudget.activities.items;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Utils.Helpers;
import com.byteshaft.mybudget.activities.MainActivity;
import com.byteshaft.mybudget.database.DBHelper;

/*
    Dialog-like activity that handles adjustments to name and amount of line item
 */

public class AdjustItemActivity extends AppCompatActivity {

    private String itemName;
    private int itemBudget;
    private int itemSpent;
    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (AppGlobals.getsCurrentMonthYear() != null) {
            myDb = new DBHelper(getApplicationContext(), AppGlobals.getsCurrentMonthYear()+".db");
        } else {
            myDb = new DBHelper(getApplicationContext(), Helpers.getTimeStamp("MMM_yyyy")+".db");
        }

        Bundle b = getIntent().getExtras();
        itemName = b.getString("ITEM_NAME");
        itemBudget = b.getInt("ITEM_BUDGET");
        itemSpent = b.getInt("ITEM_SPENT");
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

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", itemName));
        finish();
    }



    public void onSubmitClick(View v) {
        EditText newNameView = (EditText) findViewById(R.id.name);
        EditText newBudgetView = (EditText) findViewById(R.id.amount);
        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        String newName = newNameView.getText().toString();
        String newBudgetStr = newBudgetView.getText().toString();
        int newBudget;

        if (myDb.checkNameExists(newName)) {

            text = "An item with that name already exists, please try again";
            Toast.makeText(context, text, duration).show();

        } else if (newName.equals("") && newBudgetStr.equals("")) {    // no name or amount

            text = "A name or amount must be specified!";
            Toast.makeText(context, text, duration).show();
        } else if (newName.equals("") && !newBudgetStr.equals("")) { // amount but no name
            newName = itemName;
            newBudget = Integer.parseInt(newBudgetStr);
            if (newBudget < itemSpent) {
                text = "New item budget exceeds amount already spent, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.updateItem(itemName, newName, newBudget, itemSpent);
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
                finish();
            }
        } else if (!newName.equals("") && newBudgetStr.equals("")) { // name but no amount
            newBudget = itemBudget;
            if (!(newName.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {
                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();

            } else {
                myDb.updateItem(itemName, newName, newBudget, itemSpent);
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
                finish();
            }
        } else {
            newBudget = Integer.parseInt(newBudgetStr);
            if (newBudget < itemSpent) {
                text = "New item budget exceeds amount already spent, please try again";
                Toast.makeText(context, text, duration).show();
            } else if (!(newName.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {
                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.updateItem(itemName, newName, newBudget, itemSpent);
                setResult(Activity.RESULT_OK, new Intent().putExtra("ITEM_NAME", newName));
                finish();
            }
        }
    }

    public void onDeleteItemClick(View v) {

        final Intent intent = new Intent(this, MainActivity.class);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AdjustItemActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Context context = getApplicationContext();
                CharSequence text = "Item deleted";
                int duration = Toast.LENGTH_SHORT;

                // delete item from Budget table, and delete Item Table
                myDb.deleteLineItem(itemName);

                Toast.makeText(context, text, duration).show();
                // return to main activity
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete this item?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }
}
