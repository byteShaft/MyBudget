package com.byteshaft.mybudget.activities.goals;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Utils.Helpers;
import com.byteshaft.mybudget.database.DBHelper;

public class AddGoalActivity extends AppCompatActivity {

    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (AppGlobals.getsCurrentMonthYear() != null) {
            myDb = new DBHelper(getApplicationContext(), AppGlobals.getsCurrentMonthYear()+".db");
        } else {
            myDb = new DBHelper(getApplicationContext(), Helpers.getTimeStamp("MMM_yyyy")+".db");
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

    public void onClick(View v) {

        EditText nameView = (EditText) findViewById(R.id.name);
        EditText amountView = (EditText) findViewById(R.id.amount);
        EditText startDepositView = (EditText) findViewById(R.id.starting_deposit);

        String name = nameView.getText().toString();
        String amountStr = amountView.getText().toString();
        String startingDepositStr = startDepositView.getText().toString();

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        if(amountStr.equals("") || name.equals("")) {

            text = "Both a goal name and amount must be entered, please try again";
            Toast.makeText(context, text, duration).show();

        } else if(!(name.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {

            text = "Goal name can only contain letters, please try again";
            Toast.makeText(context, text, duration).show();

        } else {

            boolean result = myDb.addGoal(name, Integer.parseInt(amountStr));

            if(!startingDepositStr.equals("")) {
                myDb.addDeposit(name, "Initial", Integer.parseInt(startingDepositStr), false);
            }

            if(result) {
                text = "Goal added!";
                Toast.makeText(context, text, duration).show();

                finish();
            } else {
                text = "A goal with that name already exists, please try again";
                Toast.makeText(context, text, duration).show();
            }
        }
    }

}
