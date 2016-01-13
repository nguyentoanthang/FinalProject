package com.example.mac.finalproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectDetail extends Fragment {

    @Bind(R.id.list_detailProject)
    ListView lv;
    private ProjectDetailAdapter myAdapter;
    private ArrayList<String> list;
    private boolean permission;
    private ParseObject project;
    private boolean hide = true;

    public void setData(ArrayList<String> data, ParseObject obbject) {
        this.list = data;
        this.project = obbject;
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
        permission = ParseUser.getCurrentUser().getObjectId().equals(project.getParseUser("User").getObjectId());
        myAdapter = new ProjectDetailAdapter(getActivity(), list, R.layout.custom_detail_project, permission);
        lv.setAdapter(myAdapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    if (permission) {
                        showInputDialog(project);
                    }
                }
                return false;
            }
        });
        return v;

    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    private void showInputDialog(final ParseObject project) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.input_dialog, null);

        final AlertDialog.Builder alertDialog;
        final EditText projectName;
        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(dialogView);
        alertDialog.setTitle("Enter description");
        projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        projectName.setText(list.get(1));
        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String des = projectName.getText().toString();
                list.set(1, des);
                RefreshData();
                project.put("Description", des);
                project.saveInBackground();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void RefreshData() {
        myAdapter.notifyDataSetChanged();
    }
}
