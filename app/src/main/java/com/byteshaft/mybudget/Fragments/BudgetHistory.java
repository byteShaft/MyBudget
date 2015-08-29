package com.byteshaft.mybudget.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BudgetHistory extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private View mBaseView;
    private ListView mListBudgets;
    private TextView mTextView;
    private ArrayAdapter<String> modeAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.isMainActivityActive = false;
        mBaseView = inflater.inflate(R.layout.history, container, false);
        mListBudgets = (ListView) mBaseView.findViewById(R.id.listViewBudgets);
        mTextView = (TextView) mBaseView.findViewById(R.id.textView);
        mTextView.setVisibility(View.INVISIBLE);
        SharedPreferences preferences = getActivity().getSharedPreferences(AppGlobals.PREFS_NAME, 0);
        Set<String> total = preferences.getStringSet("TotalMonths", null);
        String[] totalMonth = new String[0];
        if (total == null) {

        }else {
            totalMonth = total.toArray(new String[total.size()]);
            if (totalMonth.length == 0) {
                mTextView.setText("No history present");
                mTextView.setVisibility(View.VISIBLE);
            }
        }
        modeAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, totalMonth);
        mListBudgets.setOnItemClickListener(this);
        mListBudgets.setOnItemLongClickListener(this);
        modeAdapter.notifyDataSetChanged();
        return mBaseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListBudgets.setAdapter(modeAdapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppGlobals.setCurrentMonthYear(parent.getItemAtPosition(position).toString());
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        final String value = parent.getItemAtPosition(position).toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Do you want to delete this record?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().getApplicationContext().deleteDatabase(value + ".db");
                SharedPreferences preferences = getActivity().getSharedPreferences(AppGlobals.PREFS_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(value);
                editor.remove(value + "curSpent");
                Set<String> total = preferences.getStringSet("TotalMonths", null);
                Set<String> set = new HashSet<>();
                if (!total.isEmpty() && total.size() >= 0) {
                    List<String> listFromSet = new ArrayList<>(total);
                    for (String item : listFromSet) {
                        if (!item.equals(value)) {
                            set.add(item);
                        }
                    }
                    editor.putStringSet("TotalMonths", set);
                    editor.commit();
                }
                dialogInterface.dismiss();
                String myAlarm = modeAdapter.getItem(position);
                modeAdapter.remove(myAlarm);
                modeAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
        return true;
    }
}
