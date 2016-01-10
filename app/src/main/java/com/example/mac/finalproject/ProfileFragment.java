package com.example.mac.finalproject;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @Bind(R.id.listDetail) ListView lv;
    @Bind(R.id.UserName) TextView name;
    @Bind(R.id.UserEmail) TextView email;
    @Bind(R.id.img) CircleImageView img;
    @Bind(R.id.alert) ImageButton alert;
    Bitmap bitmap = null;
    String username;
    String useremail;
    private boolean hide = true;
    ArrayList<String> listDetail = null;
    ItemAdapter itemAdapter;
    PopupWindow popupWindow;
    NotificationAdapter notificationAdapter;

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

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View popupView = layoutInflater.inflate(R.layout.popup_notification, null);
        popupWindow = new PopupWindow(popupView, 530, 150);
        final ListView listViewNotify = (ListView) popupView.findViewById(R.id.listNotify);
        final TextView no_notify = (TextView) popupView.findViewById(R.id.no_notify);

        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing() == false) {
                    final ArrayList<ParseObject> listNotification = getNotifyForCurrentUser();
                    if (listNotification.size() == 0) {
                        popupWindow.showAsDropDown(alert, -430, 20);
                    } else {
                        notificationAdapter = new NotificationAdapter(getActivity(), listNotification, R.layout.noification_row);
                        listViewNotify.setVisibility(View.VISIBLE);
                        listViewNotify.setAdapter(notificationAdapter);
                        listViewNotify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String workId = listNotification.get(position).getString("forWork");
                                String email = listNotification.get(position).getString("User");
                                String message = listNotification.get(position).getString("Message");
                                showChossenDialog(workId, message, email);

                            }
                        });
                        no_notify.setVisibility(View.GONE);
                        popupWindow.showAsDropDown(alert, -430, 20);
                    }

                } else {
                    popupWindow.dismiss();
                }
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
        popupWindow.dismiss();
    }

    public boolean isHide() {
        return hide;
    }

    public void RefreshData() {
        itemAdapter.notifyDataSetChanged();
    }

    public ArrayList<ParseObject> getNotifyForCurrentUser() {
        ArrayList<ParseObject> list = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
        query.whereEqualTo("User", ParseUser.getCurrentUser().getEmail());
        List<ParseObject> listNotify;
        try {
            listNotify = query.find();
            int n = listNotify.size();
            for (int i = 0; i < n; i++) {

                list.add(listNotify.get(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void showChossenDialog(final String workId, String message, final String email) {

        AlertDialog.Builder alertDialogBuider = new AlertDialog.Builder(getActivity());
        alertDialogBuider.setTitle("Invite");
        alertDialogBuider.setMessage(message);
        alertDialogBuider.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseQuery<ParseObject> workQuery = ParseQuery.getQuery("Work");
                workQuery.whereEqualTo("objectId", workId);
                ParseObject work;
                try {
                    work = workQuery.find().get(0);
                    work.addUnique("ListMember", email);
                    work.saveInBackground();

                    ParseUser.getCurrentUser().addUnique("Project", work.getParseObject("Project").getObjectId());
                    ParseUser.getCurrentUser().saveInBackground();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (ParseUser.getCurrentUser().getInt("Badge") != 0) {
                    ParseUser.getCurrentUser().increment("Badge", -1);
                    ParseUser.getCurrentUser().saveInBackground();
                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if (ParseUser.getCurrentUser().getInt("Badge") != 0) {
                    ParseUser.getCurrentUser().increment("Badge", -1);
                    ParseUser.getCurrentUser().saveInBackground();
                }
            }
        });

        AlertDialog alert = alertDialogBuider.create();
        alert.show();
    }
}
