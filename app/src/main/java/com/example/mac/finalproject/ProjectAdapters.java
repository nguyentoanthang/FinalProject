package com.example.mac.finalproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ProjectAdapters extends RecyclerView.Adapter<ProjectAdapters.ProjectViewHolder> {


    private ArrayList<Project> contactList;
    private AdapterCommunication adapterCommunication;

    public ProjectAdapters(ArrayList<Project> contactList, AdapterCommunication a) {
        this.contactList = contactList;
        this.adapterCommunication = a;
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder projectViewHolder, int i) {
        final Project currentProject = contactList.get(i);
        // update ui
        projectViewHolder.name.setText(currentProject.getName());
        projectViewHolder.done.setText(String.valueOf(currentProject.getNumOfDone()));
        projectViewHolder.sum.setText(String.valueOf(currentProject.getNumOfWork()));
        projectViewHolder.host.setImageBitmap(currentProject.getHost());
        if (currentProject.getNumOfWork() == 0) {

        } else {
            projectViewHolder.progressBar.setProgress(currentProject.getNumOfDone() / currentProject.getNumOfWork());
        }


        final int index = i;

        projectViewHolder.host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterCommunication.Callback(v, index);
            }
        });

        projectViewHolder.choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterCommunication.Callback(v, index);
            }
        });
        projectViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterCommunication.Callback(v, index);
            }
        });
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.project_layout, viewGroup, false);
        ProjectViewHolder projectViewHolder = new ProjectViewHolder(itemView);

        return projectViewHolder;
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder{

        protected CircleImageView host;
        protected TextView name;
        protected TextView done;
        protected TextView sum;
        protected ProgressBar progressBar;
        protected ImageButton choice;
        protected ImageButton arrow;

        public ProjectViewHolder(View v) {
            super(v);
            host = (CircleImageView) v.findViewById(R.id.host);
            name = (TextView) v.findViewById(R.id.nameProject);
            sum = (TextView) v.findViewById(R.id.txtsum);
            done = (TextView) v.findViewById(R.id.txtdone);
            progressBar = (ProgressBar) v.findViewById(R.id.done);
            choice = (ImageButton) v.findViewById(R.id.choice);
            arrow = (ImageButton) v.findViewById(R.id.arrow);
        }
    }
}
