package com.example.mac.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @Bind(R.id.listDetail) ListView lv;
    @Bind(R.id.UserName) TextView name;
    @Bind(R.id.UserEmail) TextView email;
    @Bind(R.id.img)
    CircleImageView img;
    Bitmap bitmap = null;
    String username;
    String useremail;
    private boolean hide = true;
    ArrayList<String> listDetail = null;
    ItemAdapter itemAdapter;

    public void setData(String name, String email, Bitmap bitmap, ArrayList<String> list) {
        this.username = name;
        this.useremail = email;
        this.bitmap = bitmap;
        this.listDetail = list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void showInputDialog(final int index) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View dialogView = layoutInflater.inflate(R.layout.input_dialog, null);
        final int currentVersion = Build.VERSION.SDK_INT;
        final AlertDialog.Builder alertDialog;
        final EditText projectName;
        if (currentVersion >= 20) {
            alertDialog = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
            alertDialog.setView(dialogView);
            projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
            switch (index) {
                case 0:
                    alertDialog.setTitle("Enter your name");
                    projectName.setHint(listDetail.get(0));
                    break;
                case 1:
                    alertDialog.setTitle("Enter your email");
                    projectName.setHint(listDetail.get(1));
                    break;
                case 2:
                    alertDialog.setTitle("Enter your phone");
                    projectName.setHint(listDetail.get(2));
                    break;
            }


            projectName.setTextColor(getResources().getColor(R.color.white));
        } else {
            alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setView(dialogView);
            alertDialog.setTitle("Enter project name");
            projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
            switch (index) {
                case 0:
                    alertDialog.setTitle("Enter your name");
                    projectName.setHint(listDetail.get(0));
                    break;
                case 1:
                    alertDialog.setTitle("Enter your email");
                    projectName.setHint(listDetail.get(1));
                    break;
                case 2:
                    alertDialog.setTitle("Enter your phone");
                    projectName.setHint(listDetail.get(2));
                    break;
            }
        }

        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String detail = projectName.getText().toString();
                switch (index) {
                    case 0:
                        ParseUser.getCurrentUser().put("Name", detail);
                        listDetail.set(0, detail);
                        break;
                    case 1:
                        ParseUser.getCurrentUser().put("email", detail);
                        listDetail.set(1, detail);
                        break;
                    case 2:
                        ParseUser.getCurrentUser().put("PhoneNumber", detail);
                        listDetail.set(2, detail);
                        break;
                }
                ParseUser.getCurrentUser().saveInBackground();
                RefreshData();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, v);

        img.setImageBitmap(bitmap);
        name.setText(username);
        email.setText(useremail);

        itemAdapter = new ItemAdapter(getActivity(), listDetail, R.layout.row);
        lv.setAdapter(itemAdapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showInputDialog(position);
                return true;
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        hide = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hide = true;
    }

    public boolean isHide() {
        return hide;
    }

    public void RefreshData() {
        itemAdapter.notifyDataSetChanged();
    }
}
