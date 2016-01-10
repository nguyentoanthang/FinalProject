package com.example.mac.finalproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.WorkViewHolder> {


    private ArrayList<Work> contactList;
    private WorkAdapterCommunication workAdapterCommunication;

    public WorkAdapter(ArrayList<Work> contactList, WorkAdapterCommunication w) {
        this.contactList = contactList;
        this.workAdapterCommunication = w;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(WorkViewHolder projectViewHolder, int i) {
        final Work currentWork = contactList.get(i);


        final int index = i;
        // update ui
        projectViewHolder.name.setText(currentWork.getName());
        if (currentWork.isForCurrentUser() == false) {
            projectViewHolder.name.setBackgroundResource(R.color.black);
        }
        projectViewHolder.choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workAdapterCommunication.WorkCallBack(v, currentWork);
            }
        });
    }

    @Override
    public WorkViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.work_layout, viewGroup, false);
        WorkViewHolder projectViewHolder = new WorkViewHolder(itemView);

        return projectViewHolder;
    }

    public static class WorkViewHolder extends RecyclerView.ViewHolder{

        protected TextView name;
        protected ImageButton choice;

        public WorkViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.nameWork);
            choice = (ImageButton) v.findViewById(R.id.choice);

        }

    }

}
