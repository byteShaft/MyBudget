package com.byteshaft.mybudget.activities.goals;

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

import com.byteshaft.mybudget.Fragments.GoalsFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.database.DBHelper;

public class AdjustGoalActivity extends AppCompatActivity {

    private DBHelper myDb;
    private String oldName;
    private int goalAmount;
    private int deposited;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_goal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDb = DBHelper.getInstance(this);
        Bundle b = getIntent().getExtras();
        oldName = b.getString("GOAL_NAME");
        deposited = b.getInt("GOAL_DEPOSITED");
        goalAmount = b.getInt("GOAL_AMOUNT");
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

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK, new Intent().putExtra("GOAL_NAME", oldName));
        finish();
    }


    public void onSubmitClick(View v) {

        EditText newNameView = (EditText) findViewById(R.id.name);
        EditText newGoalView = (EditText) findViewById(R.id.amount);

        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        String newName = newNameView.getText().toString();
        String newGoalStr = newGoalView.getText().toString();
        int newGoal;

        if(myDb.checkGoalExists(newName)) {

            text = "A goal with that name already exists, please try again";
            Toast.makeText(context, text, duration).show();

        } else if (newName.equals("") && newGoalStr.equals("")) {    // no name or amount

            text = "A name or amount must be specified!";
            Toast.makeText(context, text, duration).show();

        } else if(newName.equals("") && !newGoalStr.equals("")) { // amount but no name
            newName = oldName;
            newGoal = Integer.parseInt(newGoalStr);

            if(newGoal < deposited) {

                text = "New goal amount is less than current amount deposited, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.adjustGoal(oldName, newName, newGoal);
                setResult(Activity.RESULT_OK, new Intent().putExtra("GOAL_NAME", newName));
                finish();

            }

        } else if(!newName.equals("") && newGoalStr.equals("")) { // name but no amount
            newGoal = goalAmount;

            if(!(newName.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {

                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();

            } else {
                myDb.adjustGoal(oldName, newName, newGoal);
                setResult(Activity.RESULT_OK, new Intent().putExtra("GOAL_NAME", newName));
                finish();
            }

        } else {
            newGoal = Integer.parseInt(newGoalStr);

            if(newGoal < deposited) {

                text = "New goal amount is less than current amount deposited, please try again";
                Toast.makeText(context, text, duration).show();

            } else if(!(newName.replaceAll("\\s+", "")).matches("[a-zA-z]+")) {

                text = "Name can only contain letters, please try again";
                Toast.makeText(context, text, duration).show();

            } else {

                myDb.adjustGoal(oldName, newName, newGoal);
                setResult(Activity.RESULT_OK, new Intent().putExtra("GOAL_NAME", newName));
                finish();

            }
        }

    }

    public void onDeleteGoalClick(View v) {

        final Intent intent = new Intent(this, GoalsFragment.class);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                AdjustGoalActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Context context = getApplicationContext();
                CharSequence text = "Goal deleted";
                int duration = Toast.LENGTH_SHORT;

                // delete item from Budget table, and delete Item Table
                myDb.deleteGoal(oldName);

                Toast.makeText(context, text, duration).show();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete this goal?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }
}
