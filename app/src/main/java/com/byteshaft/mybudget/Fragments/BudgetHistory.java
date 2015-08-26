package com.byteshaft.mybudget.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.activities.MainActivity;

import java.util.Set;

public class BudgetHistory extends Fragment implements AdapterView.OnItemClickListener {

    private View mBaseView;
    private ListView mListBudgets;
    private TextView mTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.isMainActivityActive = false;
        mBaseView = inflater.inflate(R.layout.history, container, false);
        mListBudgets = (ListView) mBaseView.findViewById(R.id.listViewBudgets);
        mTextView = (TextView) mBaseView.findViewById(R.id.textView);
        mTextView.setVisibility(View.INVISIBLE);
        SharedPreferences preferences = getActivity().getSharedPreferences(HomeFragment.PREFS_NAME, 0);
        Set<String> total = preferences.getStringSet("TotalMonths", null);
        String[] totalMonth = total.toArray(new String[total.size()]);
        if (totalMonth.length == 0) {
            mTextView.setText("No history present");
            mTextView.setVisibility(View.VISIBLE);
        }
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, totalMonth);
        mListBudgets.setAdapter(modeAdapter);
        mListBudgets.setOnItemClickListener(this);
        return mBaseView;

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppGlobals.setCurrentMonthYear(parent.getItemAtPosition(position).toString());
        startActivity(new Intent(getActivity(), MainActivity.class));
    }
}
