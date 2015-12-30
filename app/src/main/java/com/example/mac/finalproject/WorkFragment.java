package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorkFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    @Bind(R.id.listWork) ListView lvWork;
    private ArrayList<Work> listWork = null;
    private WorkAdapter myAdapter;
    private boolean hide;

    public void setList(ArrayList<Work> list) {
        this.listWork = list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hide = true;
    }

    public void refreshData() {
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
        lvWork.setOnItemClickListener(this);
        lvWork.setOnItemLongClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            ((OnNewItemClickListener) getActivity()).OnItemCick(position);
        } catch (ClassCastException e) {

        }
    }

    public interface OnNewItemClickListener {
        public void OnItemCick(int Position);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            ((OnNewItemLongClick) getActivity()).OnNewItemLongClick(position);
        } catch (ClassCastException e) {

        }

        return true;
    }

    public interface OnNewItemLongClick {
        public void OnNewItemLongClick(int position);
    }
}
