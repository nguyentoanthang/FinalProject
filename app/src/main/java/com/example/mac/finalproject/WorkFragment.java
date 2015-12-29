package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorkFragment extends Fragment{

    @Bind(R.id.listWork) ListView lvWork;
    private ArrayList<Work> listWork = null;
    private WorkAdapter myAdapter;
    private boolean hide;

    public void setList(ArrayList<Work> list) {
        this.listWork = list;
    }

    public void refrreshData() {
        myAdapter.notifyDataSetChanged();
    }

    public void updateData(Work newWork) {
        listWork.add(newWork);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.work_fragment, container, false);

        ButterKnife.bind(this, v);
        myAdapter = new WorkAdapter(getActivity(), R.layout.work_layout, listWork);
        lvWork.setAdapter(myAdapter);

        return v;
    }

    public boolean isHide() {
        return hide;
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
}
