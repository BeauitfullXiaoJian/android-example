package com.example.cool1024.android_example.fragments.FlvFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.R;

import java.io.Serializable;

public class FlvDetailFragment extends Fragment {

    private static final String FLV_DETAIL_PARAM = "FLV_DETAIL_PARAM";

    public static FlvDetailFragment newInstance(Serializable flvDetail) {
        FlvDetailFragment fragment = new FlvDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(FLV_DETAIL_PARAM, flvDetail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments().getSerializable(FLV_DETAIL_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flv_detail, container, false);
    }

}
