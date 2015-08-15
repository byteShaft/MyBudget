package com.byteshaft.mybudget.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.mybudget.activities.goals.AddGoalActivity;
import com.byteshaft.mybudget.activities.goals.GoalHistoryActivity;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.activities.MainActivity;
import com.byteshaft.mybudget.adapters.GoalAdapter;
import com.byteshaft.mybudget.database.DBHelper;

public class GoalsFragment extends Fragment {

    private View baseView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private DBHelper db;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseView = inflater.inflate(R.layout.activity_goals, container, false);
        Toolbar toolbar = (Toolbar) baseView.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Savings Goals");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        mRecyclerView = (RecyclerView) baseView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        fab = (FloatingActionButton) baseView.findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);

        db = DBHelper.getInstance(getActivity());

        initGoals();

        return baseView;
    }

    public void onResume() {
        super.onResume();

        initGoals();
    }

    public void initGoals() {

        db.checkGoalTableIsDefined();

        ArrayList goals = db.getAllGoals();
        TextView placeholder = (TextView) baseView.findViewById(R.id.goal_placeholder);

        if(goals.size() > 0) {
            placeholder.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new GoalAdapter(goals);
            mRecyclerView.setAdapter(mAdapter);
            fab.show();
        } else {
            placeholder.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mRecyclerView.setAdapter(null);
            fab.hide();
        }

    }

    public void onItemClick(View v) {

        RelativeLayout holder = (RelativeLayout) v;

        Intent intent = new Intent(getActivity(), GoalHistoryActivity.class);
        intent.putExtra("GOAL_NAME", ((TextView) holder.findViewById(R.id.item_name)).getText().toString());
        startActivity(intent);

    }

    public void onAddClick(View v) {

        Intent intent = new Intent(getActivity(), AddGoalActivity.class);
        startActivity(intent);

    }

    public void onBudgetClick(View v) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void clearGoals() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.deleteGoals();

                Context context = getActivity();
                CharSequence text = "Goals deleted";
                int duration = Toast.LENGTH_SHORT;

                Toast.makeText(context, text, duration).show();

                initGoals();

            }

        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.setMessage("Are you sure you want to delete your goals?");
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_goals, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_goals) {
            clearGoals();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
