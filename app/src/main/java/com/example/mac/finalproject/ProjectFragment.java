package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectFragment extends Fragment {

    @Bind(R.id.listProject) ListView lvProject;
    ArrayList<Project> listProject = null;
    ProjectAdapter myAdapter = null;
    private final String TAG = "myTAG";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listProject = new ArrayList<>();
        Log.d(TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.project_fragment, container, false);
        Log.d(TAG, "onCreateView");
        ButterKnife.bind(this, v);

        //DataPasser dataPasser = (DataPasser) this.getArguments().getSerializable("project");
        //ArrayList<Project> list = dataPasser.getListProject();
        listProject.add(new Project("HelloWorld", 2));
        listProject.add(new Project("Blink", 12));
        //for (int i = 0; i < list.size(); i++) {
          //  listProject.add(list.get(i));
        //}
        myAdapter = new ProjectAdapter(getActivity(), R.layout.project_layout, listProject);
        lvProject.setAdapter(myAdapter);
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
        Log.d(TAG, "onDestroyView");
    }


    @Override
    public void onResume() {
        super.onResume();
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
}
