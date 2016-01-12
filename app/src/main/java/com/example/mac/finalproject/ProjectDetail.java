package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectDetail extends Fragment {

    @Bind(R.id.list_detailProject)
    ListView lv;
    private ProjectDetailAdapter myAdapter;
    ArrayList<String> list;
    private boolean hide = true;

    public void setData(ArrayList<String> data) {
        this.list = data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hide = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        hide = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.project_detail, container, false);

        ButterKnife.bind(this, v);

        myAdapter = new ProjectDetailAdapter(getActivity(), list, R.layout.custom_detail_project);
        lv.setAdapter(myAdapter);

        return v;

    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }
}
