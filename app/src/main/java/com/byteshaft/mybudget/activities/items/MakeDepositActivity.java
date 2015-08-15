package com.byteshaft.mybudget.activities.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.database.DBHelper;


public class MakeDepositActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DBHelper myDb;
    private String itemName;
    private String goalName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_deposit);

        Bundle b = getIntent().getExtras();
        itemName = b.getString("ITEM_NAME");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(itemName + ": Make Goal Deposit");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myDb = DBHelper.getInstance(this);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myDb.getGoalNames());

        if(adapter.getCount() > 0) {
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        } else {
            Context context = getApplicationContext();
            CharSequence text = "No goals have been defined yet.";
            int duration = Toast.LENGTH_SHORT;

            Toast.makeText(context, text, duration).show();

            finish();
        }


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        goalName = (String) parent.getItemAtPosition(pos);

    }

    public void onNothingSelected(AdapterView<?> parent) {}

    public void onMakeDepositClick(View v) {

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        EditText amountHolder = (EditText) findViewById(R.id.amount);
        String amountStr = amountHolder.getText().toString();

        if(amountStr.equals("")) {

            text = "Deposit amount must be specified, please try again";
            Toast.makeText(context, text, duration).show();

        } else {

            int amount = Integer.parseInt(amountStr);

            if(amount > myDb.getLineItem(itemName).getRemaining()) {

                text = "Deposit exceeds remaining budget for that item, please try again!";
                Toast.makeText(context, text, duration).show();

            } else {

                int goalRemaining = myDb.getGoalRemaining(goalName);

                if(amount > goalRemaining) {
                    text = "Deposit amount exceeds remaining amount for that goal (£" + goalRemaining + ".00), please try again!";
                    Toast.makeText(context, text, duration).show();
                } else {
                    myDb.addDeposit(goalName, itemName, amount, true);

                    if(myDb.getGoalRemaining(goalName) == 0) {
                        text = "Congratulations, you complete your goal: " + goalName + "!";
                    } else {
                        text = "Deposit added!";
                    }

                    Toast.makeText(context, text, duration).show();
                    finish();

                }

            }

        }


    }

}
