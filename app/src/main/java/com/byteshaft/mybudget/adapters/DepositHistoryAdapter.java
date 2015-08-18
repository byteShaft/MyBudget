package com.byteshaft.mybudget.adapters;

import android.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.containers.Expense;


/*
    RecyclerView Adapter that lists deposits for a Goal
 */
public class DepositHistoryAdapter extends RecyclerView.Adapter<DepositHistoryAdapter.ViewHolder>
        implements View.OnClickListener {
    private ArrayList mDataset;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tableLayout:
                System.out.println("OK");
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TableLayout mTable;
        public ViewHolder(TableLayout v) {
            super(v);
            mTable = v;
        }
    }

    public DepositHistoryAdapter(ArrayList myDataset) {

        mDataset = myDataset;
    }

    @Override
    public DepositHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        TableLayout tableLayout = (TableLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_table, parent, false);
        ViewHolder vh = new ViewHolder(tableLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Expense cur;
        TableLayout curTable = holder.mTable;

        TextView historyDate = (TextView) curTable.findViewById(R.id.history_date);
        TextView historyName = (TextView) curTable.findViewById(R.id.history_name);
        TextView historyAmount = (TextView) curTable.findViewById(R.id.history_amount);
        cur = (Expense) mDataset.get(position);
        TableLayout layout = (TableLayout) curTable.findViewById(R.id.tableLayout);
        layout.setOnClickListener(this);
        historyDate.setText(cur.getDate());
        historyName.setText(cur.getName());
        historyAmount.setText("£" + Integer.toString(cur.getAmount()) + ".00");

    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }
}