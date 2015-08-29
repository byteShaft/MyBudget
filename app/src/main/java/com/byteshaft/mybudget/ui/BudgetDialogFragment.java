package com.byteshaft.mybudget.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.AppGlobals;
import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.Utils.Helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BudgetDialogFragment extends DialogFragment {

    private float budget;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("Set budget limit");
        builder.setView(inflater.inflate(R.layout.dialog_budget, null))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog myDialog = (Dialog) dialog;
                        EditText myEditText = (EditText) myDialog.findViewById(R.id.budgetDialog);
                        if (TextUtils.isEmpty(myEditText.getText())) {
                            Toast.makeText(getActivity(), "please enter amount", Toast.LENGTH_SHORT).show();
                        } else {
                            budget = Integer.parseInt(myEditText.getText().toString());
                            SharedPreferences preferences = getActivity().getSharedPreferences(AppGlobals.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            if (AppGlobals.getDatePickerState() || AppGlobals.getDpCurrentMonthExist()) {
                                editor.putFloat(AppGlobals.getDatePickerValues(), Float.valueOf(myEditText.getText().toString()));
                            } else if (AppGlobals.getsCurrentMonthYear() != null) {
                                editor.putFloat(AppGlobals.getsCurrentMonthYear(), Float.valueOf(myEditText.getText().toString()));
                            } else {
                                editor.putFloat(Helpers.getTimeStamp("MMM_yyyy"), Float.valueOf(myEditText.getText().toString()));
                            }
                            Set<String> items = new HashSet<>();
                            Set<String> totalMonth = preferences.getStringSet("TotalMonths", null);
                            if (totalMonth != null || AppGlobals.getDpCurrentMonthExist() ||
                                    AppGlobals.getDatePickerState() ||
                                    AppGlobals.getsCurrentMonthYear() != null || AppGlobals.getBudgetCleared()) {   
                                System.out.println("Working");
                                if (AppGlobals.getDatePickerState() || AppGlobals.getDpCurrentMonthExist()) {
                                    items.add(AppGlobals.getDatePickerValues());
                                    System.out.println(AppGlobals.getDatePickerValues());
                                } else if (AppGlobals.getsCurrentMonthYear() != null) {
                                    items.add(AppGlobals.getsCurrentMonthYear());
                                } else if (AppGlobals.getBudgetCleared()) {
                                    items.add(Helpers.getTimeStamp("MMM_yyyy"));
                                    System.out.println("that");
                                }
                                List<String> listFromSet = new ArrayList<>(totalMonth);
                                for (String item : listFromSet) {
                                    items.add(item);
                                    System.out.println(item);
                                }
                                editor.putStringSet("TotalMonths", items);
                                editor.commit();
                            } else {
                                System.out.println("elsePart");
                                Set<String> set = new HashSet<>();
                                set.add(Helpers.getTimeStamp("MMM_yyyy"));
                                editor.putStringSet("TotalMonths", set);
                                editor.commit();
                            }
                            FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                            tx.replace(R.id.container, new HomeFragment());
                            tx.commit();
                        }
                    }
                });
        return builder.create();
    }
    public float getBudget() {
        return budget;

    }
}
