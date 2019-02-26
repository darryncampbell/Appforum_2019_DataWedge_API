package com.darryncampbell.configuredatawedge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ScannerListAdapter extends RecyclerView.Adapter<ScannerListAdapter.MyViewHolder> {
    private ArrayList<Scanner> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView scanner_name;
        public TextView scanner_id;
        public TextView scanner_index;
        public TextView scanner_state;
        public MyViewHolder(View v) {
            super(v);
            scanner_name = (TextView) v.findViewById(R.id.scanner_name);
            scanner_id = (TextView) v.findViewById(R.id.scanner_id);
            scanner_index = (TextView) v.findViewById(R.id.scanner_index);
            scanner_state = (TextView) v.findViewById(R.id.scanner_state);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ScannerListAdapter(ArrayList<Scanner> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ScannerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View scannerView = inflater.inflate(R.layout.scanner_row_layout, parent, false);

        // Return a new holder instance
        ScannerListAdapter.MyViewHolder viewHolder = new ScannerListAdapter.MyViewHolder(scannerView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.scanner_name.setText(mDataset.get(position).getName());
        holder.scanner_id.setText("ID: " + mDataset.get(position).getId());
        holder.scanner_index.setText("Index: " + mDataset.get(position).getIndex() + "");
        holder.scanner_state.setText("Connected?: "+ mDataset.get(position).getState() + "");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}