package com.example.mac.finalproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HeaderFragment extends Fragment {

    @Bind(R.id.profile_image) ImageView _imageProfile;
    @Bind(R.id.name) TextView _name;
    @Bind(R.id.email) TextView _email;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.drawer_header, container, false);

        ButterKnife.bind(this, v);

        return v;
    }

    @Nullable
    @Override
    public View getView() {
        return v;
    }
}
