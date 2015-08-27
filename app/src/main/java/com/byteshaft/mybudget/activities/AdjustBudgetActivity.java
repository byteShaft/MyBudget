package com.byteshaft.mybudget.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.Utils.Helpers;
import com.byteshaft.mybudget.database.DBHelper;
/*
 Prompts user to enter a new budget. Called from MainActivity.
 */
public class AdjustBudgetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button submit;
    private EditText amountHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_adjust_budget);
        submit = (Button) findViewById(R.id.on_submit);
        submit.setOnClickListener(this);
        amountHolder = (EditText) findViewById(R.id.amount_holder);
        amountHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    submit.setEnabled(false);
                } else {
                    submit.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    /*
    Called by button click in layout file.
    */
    private void onSubmit() {
        float newBudget = Float.parseFloat(amountHolder.getText().toString());

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;
        DBHelper myDb;

        // check that budget does not exceed amount already allocated
        if (AppGlobals.getsCurrentMonthYear() != null) {
            myDb = new DBHelper(getApplicationContext(), AppGlobals.getsCurrentMonthYear()+".db");
        } else {
            myDb = new DBHelper(getApplicationContext(), Helpers.getTimeStamp("MMM_yyyy")+".db");
        }

        if (newBudget < myDb.getTotalAllocated()) {
            text = "New budget amount is less than amount already allocated, please try again";
            Toast.makeText(context, text, duration).show();
        } else {
            SharedPreferences prefs = getSharedPreferences(AppGlobals.PREFS_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(Helpers.getTimeStamp("MMM_yyyy"), newBudget);
            boolean result = editor.commit();

            if (result) {
                text = "Budget adjusted";
                Toast.makeText(context, text, duration).show();
            } else {
                text = "Failed to adjust budget";
                Toast.makeText(context, text, duration).show();
            }

            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.on_submit:
                onSubmit();
                break;
        }
    }
}