package com.example.cool1024.android_example.fragments.FlvFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.R;

public class FlvCommentFragment extends Fragment {

    private static final String FLV_ID_PARAM = "FLV_ID_PARAM";

    public static FlvCommentFragment newInstance(int flvId) {
        FlvCommentFragment fragment = new FlvCommentFragment();
        Bundle args = new Bundle();
        args.putInt(FLV_ID_PARAM, flvId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments().getString(FLV_ID_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flv_comment, container, false);
    }
}
