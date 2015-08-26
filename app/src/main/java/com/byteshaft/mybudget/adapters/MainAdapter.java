package com.byteshaft.mybudget.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.byteshaft.mybudget.R;
import com.byteshaft.mybudget.activities.items.ItemHistoryActivity;
import com.byteshaft.mybudget.containers.LineItem;
/*
    RecyclerView Adapter that displays line items in MainActivity
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private ArrayList mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mLayout;
        public ViewHolder(RelativeLayout v) {
            super(v);
            mLayout = v;
        }
    }

    public MainAdapter(ArrayList myDataset) {

        mDataset = myDataset;
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                        int viewType) {

        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v,parent.getContext());
            }
        });
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LineItem cur = (LineItem) mDataset.get(position);
        RelativeLayout curItem = holder.mLayout;

        double progress = (cur.getSpent()/(double)cur.getBudget()) * 100;

        TextView curTitle = (TextView) curItem.findViewById(R.id.item_name);
        TextView curProgText = (TextView) curItem.findViewById(R.id.prog_text);
        ProgressBar curProgBar = (ProgressBar) curItem.findViewById(R.id.prog_bar);

        curTitle.setText(cur.getName());
        curProgText.setText("€" + Integer.toString(cur.getSpent()) + ".00/€" + Integer.toString(cur.getBudget()) + ".00");
        curProgBar.setProgress((int) progress);

    }

    public void onItemClick(View v,Context context) {
        RelativeLayout callHolder = (RelativeLayout) v;
        String itemName = ((TextView) callHolder.findViewById(R.id.item_name)).getText().toString();
        Intent intent = new Intent(context, ItemHistoryActivity.class);
        intent.putExtra("ITEM_NAME", itemName);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }

}