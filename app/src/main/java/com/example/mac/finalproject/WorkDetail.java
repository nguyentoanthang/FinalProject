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

public class WorkDetail extends Fragment {

    @Bind(R.id.list_detailWork)
    ListView lv;
    private WorkDetailAdapter myAdapter;
    ArrayList<String> list;
    private boolean hide = true;

    public void setData(ArrayList<String> data) {
        this.list = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.work_detail, container, false);

        ButterKnife.bind(this, v);

        myAdapter = new WorkDetailAdapter(getActivity(), list, R.layout.custom_detail_work);
        lv.setAdapter(myAdapter);

        return v;
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

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }
}
