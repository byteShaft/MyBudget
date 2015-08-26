package com.byteshaft.mybudget.activities.goals;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Utils.Helpers;
import com.byteshaft.mybudget.adapters.DepositHistoryAdapter;
import com.byteshaft.mybudget.containers.Goal;
import com.byteshaft.mybudget.database.DBHelper;

public class GoalHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DBHelper myDb;
    private String name;
    private Goal myGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CardView overviewCard = (CardView) findViewById(R.id.overview_card);

        View.OnLongClickListener listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(1000);

                Toast toast = Toast.makeText(getApplicationContext(), v.getContentDescription(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.show();
                return true;
            }
        };

        overviewCard.setOnLongClickListener(listener);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (AppGlobals.getsCurrentMonthYear() != null) {
            myDb = new DBHelper(getApplicationContext(), AppGlobals.getsCurrentMonthYear()+".db");
        } else {
            myDb = new DBHelper(getApplicationContext(), Helpers.getTimeStamp("MMM_yyyy")+".db");
        }

        Bundle b = getIntent().getExtras();

        if(b != null) {
            name = b.getString("GOAL_NAME");
            getSupportActionBar().setTitle("Saving: " + name);

            myGoal = myDb.getGoal(name);

            if(myGoal != null) {
                setOverview();
                setHistory();
            }
        }
        else {
            getSupportActionBar().setTitle("Item Name Not Found");

            Context context = getApplicationContext();
            CharSequence text = "Item name was not provided";
            int duration = Toast.LENGTH_SHORT;

            Toast.makeText(context, text, duration).show();

            finish();
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

    @Override
    public void onResume() {
        super.onResume();

        setOverview();
        setHistory();
    }

    public void setOverview() {

        myGoal = myDb.getGoal(name);

        TextView goal = (TextView) findViewById(R.id.goal);
        TextView deposited = (TextView) findViewById(R.id.deposited);
        TextView remaining = (TextView) findViewById(R.id.remaining);

        goal.setText("Saving: £" + Integer.toString(myGoal.getGoal()) + ".00");
        deposited.setText("Deposited: £" + Integer.toString(myGoal.getDeposited()) + ".00");
        remaining.setText("Remaining: £" + Integer.toString(myGoal.getGoal() - myGoal.getDeposited()) + ".00");

    }

    public void setHistory() {

        TextView placeholder = (TextView) findViewById(R.id.deposit_history_placeholder);
        ArrayList history = myDb.getDepositHistory(name);

        if(history.size() != 0) {
            placeholder.setVisibility(View.GONE);

            RecyclerView.Adapter mAdapter = new DepositHistoryAdapter(history);
            mRecyclerView.setAdapter(mAdapter);
        } else {

            placeholder.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(null);

        }
    }

    public void onOverviewClick(View v) {
        int REQUEST_GOAL_ADJUSTMENT = 0;
        Intent intent = new Intent(this, AdjustGoalActivity.class);
        intent.putExtra("GOAL_NAME", name);
        intent.putExtra("GOAL_DEPOSITED", myGoal.getDeposited());
        intent.putExtra("GOAL_AMOUNT", myGoal.getGoal());
        startActivityForResult(intent, REQUEST_GOAL_ADJUSTMENT);

    }

    public void onHistoryClick(View v) {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        name = data.getExtras().getString("GOAL_NAME");
        getSupportActionBar().setTitle("Item Name: " + name);

    }

}
