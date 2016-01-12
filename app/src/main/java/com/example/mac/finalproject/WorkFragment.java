package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorkFragment extends Fragment {
    @Bind(R.id.cardWork)
    RecyclerView rvWork;
    private ArrayList<Work> listWork = null;
    private WorkAdapter myAdapter;
    private boolean hide = true;

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

    public void updateData(ArrayList<Work> list) {
        for (int i = 0; i < list.size(); i++) {
            this.listWork.add(list.get(i));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.work_fragment, container, false);

        ButterKnife.bind(this, v);
        rvWork.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvWork.setLayoutManager(llm);

        myAdapter = new WorkAdapter(listWork, (WorkAdapterCommunication) getActivity());
        rvWork.setAdapter(myAdapter);
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

    public void removeItem(Work item) {
        listWork.remove(item);
    }
}
