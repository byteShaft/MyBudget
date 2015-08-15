package com.byteshaft.mybudget.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.activities.AdjustBudgetActivity;
import com.byteshaft.mybudget.activities.items.AddItemActivity;
import com.byteshaft.mybudget.activities.items.ItemHistoryActivity;
import com.byteshaft.mybudget.adapters.MainAdapter;
import com.byteshaft.mybudget.database.DBHelper;
import com.byteshaft.mybudget.ui.BudgetDialogFragment;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements BudgetDialogFragment.BudgetDialogListener {

    public final static String PREFS_NAME = "MyBudgetPrefs";
    private View baseView;
    private RecyclerView mRecyclerView;
    private DBHelper db;
    private CardView budgetCard;
    private FloatingActionButton fab;
    private int curBudget = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.activity_main , container, false);
        budgetCard = (CardView) baseView.findViewById(R.id.budget_card);
        budgetCard.setVisibility(View.GONE);
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        curBudget = preferences.getInt("curBudget", 0);
//        Toolbar toolbar = (Toolbar) baseView.findViewById(R.id.my_toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        getActivity().getActionBar().setTitle("Budget");
        View.OnLongClickListener listener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(1000);

                Toast toast = Toast.makeText(getActivity(), v.getContentDescription(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.show();
                return true;
            }
        };

        View.OnLongClickListener addItemListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Vibrator vb = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(1000);

                String str = "Add new item - £" + Integer.toString(curBudget - db.getTotalAllocated()) + ".00 left to allocate";

                Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.show();
                return true;
            }
        };

        //findViewById(R.id.add_item_button).setOnLongClickListener(addItemListener);
        baseView.findViewById(R.id.budget_card).setOnLongClickListener(listener);
//
//        DrawerLayout mDrawerLayout = (DrawerLayout) baseView.findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer, R.string.main);
//        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mRecyclerView = (RecyclerView) baseView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        fab = (FloatingActionButton) baseView.findViewById(R.id.fab);
        fab.setOnLongClickListener(addItemListener);
        fab.attachToRecyclerView(mRecyclerView);
        fab.show();

        db = DBHelper.getInstance(getActivity());

        if (curBudget == 0) {
            DialogFragment fragment = new BudgetDialogFragment();
            fragment.show(getFragmentManager(), "budget");
            fragment.setCancelable(false);
        } else {
            initCards();
        }
        return baseView;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
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
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        curBudget = preferences.getInt("curBudget", 0);
        db.checkBudgetIsDefined();
        int totalSpent = db.getTotalSpent();
        TextView placeholder = (TextView) baseView.findViewById(R.id.item_placeholder);
        TextView budgeted = (TextView) budgetCard.findViewById(R.id.budgeted);
        TextView spent = (TextView) budgetCard.findViewById(R.id.spent);
        TextView remaining = (TextView) budgetCard.findViewById(R.id.remaining);
        budgeted.setText("Budgeted: £" + Integer.toString(curBudget) + ".00");
        spent.setText("Spent: £" + totalSpent + ".00");
        remaining.setText("Remaining: £" + Integer.toString(curBudget - totalSpent) + ".00");
        budgetCard.setVisibility(View.VISIBLE);
        placeholder.setVisibility(View.VISIBLE);

        if (db.getNoRows() != 0)
            placeholder.setVisibility(View.GONE);

        ArrayList myLineItems = db.getAllLineItems();
        RecyclerView.Adapter mAdapter = new MainAdapter(myLineItems);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void addLineItem(View v) {
        if(curBudget - db.getTotalAllocated() == 0) {
            Toast.makeText(getActivity(),
                    "please set budget ", Toast.LENGTH_SHORT).show();
            DialogFragment fragment = new BudgetDialogFragment();
            fragment.show(getFragmentManager(), "budget");
            fragment.setCancelable(false);
        } else {
            Intent intent = new Intent(getActivity(), AddItemActivity.class);
            startActivity(intent);
        }
    }

    public void clearBudget() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.clearBudget();
                SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("curBudget", 0);
                editor.commit();
                Context context = getActivity();
                CharSequence text = "Budget cleared";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text, duration).show();
                DialogFragment fragment = new BudgetDialogFragment();
                fragment.show(getFragmentManager(), "budget");

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

        Intent intent = new Intent(getActivity(), ItemHistoryActivity.class);
        intent.putExtra("ITEM_NAME", itemName);
        startActivity(intent);
    }

    public void onGoalsClick(View v) {
        Intent intent = new Intent(getActivity(), GoalsFragment.class);
        startActivity(intent);
    }

    public void adjustBudget(View v) {
        Intent intent = new Intent(getActivity(), AdjustBudgetActivity.class);
        startActivity(intent);
    }

    // code adapted from http://stackoverflow.com/questions/6290599/prompt-user-when-back-button-is-pressed
    private void exit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }

        });
        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Do you want to quit?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
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
