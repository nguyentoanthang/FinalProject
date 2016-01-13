package com.example.mac.finalproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberFragment extends Fragment {

    private MemberAdapter adapter;
    private ArrayList<Member> list = null;
    private boolean hide = true;
    @Bind(R.id.listMember)
    ListView lv;
    private Work w;

    public void setData(ArrayList<Member> list, Work w) {
        this.w = w;
        this.list = list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.member, container, false);

        ButterKnife.bind(this, v);

        adapter = new MemberAdapter(getActivity(), R.layout.member_row, list);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (list.get(position).getEmail().equals(ParseUser.getCurrentUser().getEmail())) {
                    //showAlertDialog("Nothing to do", "He is you?");
                } else  {
                    showChoiceDialogForContact(position);
                }



                return false;
            }
        });

        return v;
    }

    public static boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return (tm.getSimState() == TelephonyManager.SIM_STATE_READY);
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialogAlert = alert.create();
        dialogAlert.show();
    }

    private void showChoiceDialogForContact(final int index) {
        final String phone = list.get(index).getPhone();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Contact");
        builder.setItems(R.array.Contact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        if (isSimSupport(getActivity())) {
                            try {
                                if (!phone.equals("")) {
                                    Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                                    my_callIntent.setData(Uri.parse("tel:" + phone));
                                    startActivity(my_callIntent);
                                } else {
                                    showAlertDialog("No phone number", "He hasn't fill his phone number yet");
                                }

                            } catch (Exception e) {
                                showAlertDialog("Error", "There is error occur when ...");
                            }
                        } else {
                            showAlertDialog("No sim", "This device hasn't have sim yet");
                        }

                    }
                    break;
                    case 1: {
                        if (isSimSupport(getActivity())) {
                            try {
                                if (!phone.equals("")) {
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.putExtra("address", phone);
                                    startActivity(smsIntent);
                                } else {
                                    showAlertDialog("No phone number", "He hasn't fill his phone number yet");
                                }

                            } catch (Exception e) {
                                showAlertDialog("Error", "There is error occur when ...");
                            }
                        } else {
                            showAlertDialog("No sim", "This device hasn't have sim yet");
                        }
                        break;
                    }
                    case 2: {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", list.get(index).getEmail(), null));
                        startActivity(emailIntent);
                        break;
                    }
                    case 3: {
                        if (w.isPermission()) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
                            query.whereEqualTo("objectId", w.getId());
                            ParseObject object;

                            try {
                                object = query.find().get(0);

                                List<String> listEmail = new ArrayList<>();
                                listEmail.add(list.get(index).getEmail());
                                Collection<String> list_member = listEmail;
                                object.removeAll("ListMember", list_member);
                                object.removeAll("LNo", list_member);
                                object.saveInBackground();
                                ParseQuery pushQuery = ParseInstallation.getQuery();
                                pushQuery.whereEqualTo("UserEmail", list.get(index).getEmail());
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("title", "Delete");
                                    data.put("alert", ParseUser.getCurrentUser().getEmail() + " remove you to their project");
                                    data.put("sender", ParseUser.getCurrentUser().getObjectId());
                                    data.put("id", object.getParseObject("Project").getObjectId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // Send push notification to query
                                ParsePush push = new ParsePush();
                                push.setQuery(pushQuery); // Set our Installation query
                                push.setData(data);
                                push.sendInBackground();

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            showAlertDialog("Permission", "You have no permission to do that");
                        }
                        break;
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
