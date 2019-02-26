package com.example.cool1024.android_example.fragments;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.R;

public class ImageScaleFragment extends BaseFragment {

    public static final String TAG = "ImageScaleFragment";

    public static ImageScaleFragment newInstance() {
        return new ImageScaleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_scale, container, false);
    }

}
