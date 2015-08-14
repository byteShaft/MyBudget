package com.byteshaft.mybudget.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.mybudget.activities.items.AddItemActivity;
import com.byteshaft.mybudget.activities.items.ItemHistoryActivity;
import com.byteshaft.mybudget.adapters.MainAdapter;
import com.byteshaft.mybudget.database.DBHelper;
import com.byteshaft.mybudget.ui.BudgetDialogFragment;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.activities.goals.GoalsActivity;


public class MainActivity extends AppCompatActivity implements BudgetDialogFragment.BudgetDialogListener{

    public final static String PREFS_NAME = "MyBudgetPrefs";

    private RecyclerView mRecyclerView;
    private DBHelper db;
    private CardView budgetCard;
    private FloatingActionButton fab;

    private int curBudget = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budgetCard = (CardView) findViewById(R.id.budget_card);
        budgetCard.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        curBudget = preferences.getInt("curBudget", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Budget");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

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

        View.OnLongClickListener addItemListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(1000);

                String str = "Add new item - $" + Integer.toString(curBudget - db.getTotalAllocated()) + ".00 left to allocate";

                Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.show();
                return true;
            }
        };

        //findViewById(R.id.add_item_button).setOnLongClickListener(addItemListener);
        findViewById(R.id.budget_card).setOnLongClickListener(listener);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer, R.string.main);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnLongClickListener(addItemListener);
        fab.attachToRecyclerView(mRecyclerView);
        fab.show();

        db = DBHelper.getInstance(this);

        if(curBudget == 0) {
            DialogFragment fragment = new BudgetDialogFragment();
            fragment.show(getSupportFragmentManager(), "budget");
        } else {
            initCards();
        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        BudgetDialogFragment mBudgetDialog = (BudgetDialogFragment) dialog;
        editor.putInt("curBudget", mBudgetDialog.getBudget());
        editor.commit();
        curBudget = mBudgetDialog.getBudget();
        initCards();
    }

    @Override
    public void onResume() {
        super.onResume();
        initCards();
    }

    public void initCards() {
        fab.show();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        curBudget = preferences.getInt("curBudget", 0);
        db.checkBudgetIsDefined();

        int totalSpent = db.getTotalSpent();

        TextView placeholder = (TextView) findViewById(R.id.item_placeholder);
        TextView budgeted = (TextView) budgetCard.findViewById(R.id.budgeted);
        TextView spent = (TextView) budgetCard.findViewById(R.id.spent);
        TextView remaining = (TextView) budgetCard.findViewById(R.id.remaining);

        budgeted.setText("Budgeted: $" + Integer.toString(curBudget) + ".00");
        spent.setText("Spent: $" + totalSpent + ".00");
        remaining.setText("Remaining: $" + Integer.toString(curBudget - totalSpent) + ".00");

        budgetCard.setVisibility(View.VISIBLE);
        placeholder.setVisibility(View.VISIBLE);

        if(db.getNoRows() != 0)
            placeholder.setVisibility(View.GONE);

        ArrayList myLineItems = db.getAllLineItems();
        RecyclerView.Adapter mAdapter = new MainAdapter(myLineItems);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void addLineItem(View v) {
        if(curBudget - db.getTotalAllocated() == 0) {
            Toast.makeText(getApplicationContext(), "Budget has been completely allocated", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, AddItemActivity.class);
            startActivity(intent);
        }

    }

    public void clearBudget() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.clearBudget();

                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("curBudget", 0);
                editor.commit();

                Context context = getApplicationContext();
                CharSequence text = "Budget cleared";
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();

                DialogFragment fragment = new BudgetDialogFragment();
                fragment.show(getSupportFragmentManager(), "budget");

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to clear your budget?\n\nAll items and expenses will be lost!");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();

    }

    public void onItemClick(View v) {

        RelativeLayout callHolder = (RelativeLayout) v;
        String itemName = ((TextView) callHolder.findViewById(R.id.item_name)).getText().toString();

        Intent intent = new Intent(this, ItemHistoryActivity.class);
        intent.putExtra("ITEM_NAME", itemName);
        startActivity(intent);

    }

    public void onGoalsClick(View v) {

        Intent intent = new Intent(this, GoalsActivity.class);
        startActivity(intent);

    }

    public void adjustBudget(View v) {

        Intent intent = new Intent(this, AdjustBudgetActivity.class);
        startActivity(intent);

    }

    // code adapted from http://stackoverflow.com/questions/6290599/prompt-user-when-back-button-is-pressed
    private void exit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Do you want to quit?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            clearBudget();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
