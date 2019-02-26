package com.darryncampbell.configuredatawedge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfilesListAdapter  extends RecyclerView.Adapter<ProfilesListAdapter.MyViewHolder> {
    private ArrayList<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView profile_name;
        public MyViewHolder(View v) {
            super(v);
            profile_name = (TextView) v.findViewById(R.id.profile_name);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProfilesListAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProfilesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View profileView = inflater.inflate(R.layout.profile_row_layout, parent, false);

        // Return a new holder instance
        ProfilesListAdapter.MyViewHolder viewHolder = new ProfilesListAdapter.MyViewHolder(profileView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProfilesListAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.profile_name.setText(mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}