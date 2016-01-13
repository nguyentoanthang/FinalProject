package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectFragment extends Fragment {

    @Bind(R.id.cardProject)
    RecyclerView rvProject;
    private ArrayList<Project> listProject = null;
    private ProjectAdapters myAdapter = null;
    private final String TAG = "myTAG";
    private boolean hide = true;

    public void setList(ArrayList<Project> list) {
        this.listProject = list;
        Log.d(TAG, "onSetList");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.project_fragment, container, false);
        Log.d(TAG, "onCreateView");
        ButterKnife.bind(this, v);
        rvProject.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvProject.setLayoutManager(llm);
        myAdapter = new ProjectAdapters(listProject, (AdapterCommunication)getActivity());
        rvProject.setAdapter(myAdapter);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hide = true;
        Log.d(TAG, "onDestroyView");
    }


    @Override
    public void onResume() {
        super.onResume();
        hide = false;
        Log.d(TAG, "onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    public boolean isHide() {
        return hide;
    }

    public void refreshData() {
        myAdapter.notifyDataSetChanged();
    }

    public void updateData(Project newProject) {
        listProject.add(newProject);
    }

    public void removeItemAtIndex(int index) {
        listProject.remove(index);
    }

    public void removeAll() {

    }
}
