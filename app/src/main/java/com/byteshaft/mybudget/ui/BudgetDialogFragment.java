package com.byteshaft.mybudget.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.mybudget.Fragments.HomeFragment;
import com.byteshaft.mybudget.R;

public class BudgetDialogFragment extends DialogFragment {

    private int budget;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (BudgetDialogListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement BudgetDialogListener");
//        }
//    }

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
                            SharedPreferences preferences = getActivity().getSharedPreferences(HomeFragment.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("curBudget", Integer.valueOf(myEditText.getText().toString()));
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
