package com.example.cool1024.android_example;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CenterFragment extends Fragment {

    public static final String TAG = "CenterFragment";

    private ImageView mBackgroundImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_center, container, false);
        findViewComponent(view);
        initView(view);
        initViewEvent(view);
        return view;
    }

    private void findViewComponent(View view){
        mBackgroundImageView = (ImageView) view.findViewById(R.id.image_bg);
    }

    private void initView(View view){
        Glide.with(CenterFragment.this)
             .load("https://hello1024.oss-cn-beijing.aliyuncs.com/upload/banner/201808310313105b88b246cb80c.jpg")
             .into(mBackgroundImageView);
    }

    private void initViewEvent(View view){
        view.findViewById(R.id.settings_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
    }

}