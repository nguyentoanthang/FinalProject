package com.example.mac.finalproject;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorkDetail extends Fragment {

    @Bind(R.id.list_detailWork)
    ListView lv;
    private WorkDetailAdapter myAdapter;
    ArrayList<String> list;
    private boolean hide = true;
    private boolean permission;
    private ParseObject work;

    public void setData(ArrayList<String> data, boolean permission, ParseObject w) {
        this.list = data;
        this.permission = permission;
        this.work = w;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.work_detail, container, false);

        ButterKnife.bind(this, v);

        myAdapter = new WorkDetailAdapter(getActivity(), list, R.layout.custom_detail_work, permission);
        lv.setAdapter(myAdapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (permission) {
                    if (position == 1) {
                        showInputDialog(work);
                    } else if (position == 5){
                        showDatePickerDialog(work);
                    }
                }
                return true;
            }
        });

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

    private void showInputDialog(final ParseObject work) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.input_dialog, null);

        final AlertDialog.Builder alertDialog;
        final EditText projectName;
        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setView(dialogView);
        alertDialog.setTitle("Enter description");
        alertDialog.setIcon(R.drawable.edit);
        projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        projectName.setText(list.get(1));
        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String des = projectName.getText().toString();

                list.set(1, des);
                RefreshData();
                work.put("Description", des);
                work.saveInBackground();
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

    public void RefreshData() {
        myAdapter.notifyDataSetChanged();
    }

    private void showDatePickerDialog(final ParseObject project) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Calendar C = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        Date d = C.getTime();

                        project.put("DeadLine", d);
                        project.saveInBackground();

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

                        list.set(5, sdf.format(d));
                        RefreshData();
                    }
                }, mYear, mMonth, mDay);
        dpd.show();

    }
}
