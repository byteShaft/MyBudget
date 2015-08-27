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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BudgetDialogFragment extends DialogFragment {

    private int budget;

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
                            editor.putInt(Helpers.getTimeStamp("MMM_yyyy"), Integer.valueOf(myEditText.getText().toString()));
                            Set<String> totalMonth = preferences.getStringSet("TotalMonths", null);
                            if (totalMonth == null) {
                                Set<String> set = new HashSet<>();
                                set.add(Helpers.getTimeStamp("MMM_yyyy"));
                                editor.putStringSet("TotalMonths",set); editor.commit();
                            } else {
                                totalMonth.add(Helpers.getTimeStamp("MMM_yyyy"));
                                editor.putStringSet("TotalMonths", totalMonth);
                            }

                            editor.commit();
                            FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                            tx.replace(R.id.container, new HomeFragment());
                            tx.commit();
                        }
                    }
                });
        return builder.create();
    }
    public int getBudget() {
        return budget;

    }
}
