package com.example.mac.finalproject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class ProjectAdapter extends ArrayAdapter<ParseObject> {

    private Activity context = null;
    private int layoutId;
    ArrayList<ParseObject> lisProject = null;

    public ProjectAdapter(Activity context, int layoutId, ArrayList<ParseObject> list) {
        super(context, layoutId, list);
        this.context = context;
        this.layoutId = layoutId;
        this.lisProject = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();

        convertView = layoutInflater.inflate(layoutId, null);

        final TextView name = (TextView) convertView.findViewById(R.id.nameProject);

        final ParseObject currentProject = lisProject.get(position);

        final CircleImageView host = (CircleImageView) convertView.findViewById(R.id.host);

        CheckInternet check = new CheckInternet(context);

        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.done);
        TextView done = (TextView) convertView.findViewById(R.id.txtdone);
        TextView sum = (TextView) convertView.findViewById(R.id.txtsum);


        if (check.isOnline()) {
            name.setText(currentProject.getString("Name"));
            ParseUser user = currentProject.getParseUser("User");
            ParseFile fileImage = (ParseFile) user.get("avatar");
            int i = currentProject.getInt("Work");
            int j = currentProject.getInt("DoneWork");
            done.setText(String.valueOf(j));
            sum.setText(String.valueOf(i));
            progressBar.setProgress(j/i);
            if (fileImage != null) {
                fileImage.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            host.setImageBitmap(bitmap);
                        } else {

                        }
                    }
                });
            } else {

            }
        } else {
            onErrorInternet();
        }

        return convertView;

    }

    public void onErrorInternet() {
        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
    }
}
