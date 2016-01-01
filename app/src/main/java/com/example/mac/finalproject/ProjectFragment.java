package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    @Bind(R.id.listProject) ListView lvProject;
    private ArrayList<ParseObject> listProject = null;
    private ProjectAdapter myAdapter = null;
    private final String TAG = "myTAG";
    private boolean hide;

    public void setList(ArrayList<ParseObject> list) {
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
        myAdapter = new ProjectAdapter(getActivity(), R.layout.project_layout, listProject);
        lvProject.setAdapter(myAdapter);
        lvProject.setOnItemClickListener(this);
        lvProject.setOnItemLongClickListener(this);
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

    public void updateData(ParseObject newProject) {
        listProject.add(newProject);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            ((OnNewItemLongClickListener) getActivity()).OnItemLongClick(position);
        } catch (ClassCastException e) {

        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            ((OnNewItemClickListener) getActivity()).OnItemPick(position);
        } catch (ClassCastException e) {

        }
    }

    public interface OnNewItemClickListener {
        public void OnItemPick(int Position);
    }

    public interface OnNewItemLongClickListener {
        public void OnItemLongClick(int position);
    }
}
