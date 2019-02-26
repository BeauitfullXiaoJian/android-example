package com.example.cool1024.android_example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.components.AnimationImageView;
import com.example.cool1024.android_example.components.FlingImageView;
import com.example.cool1024.android_example.components.ScaleImageView;
import com.example.cool1024.android_example.components.SpringImageView;

public class AnimationFragment extends BaseFragment {

    public static final String TAG = "AnimationFragment";
    public static final String ANIMATION_NAME_PARAM = "ANIMATION_NAME_PARAM";

    private int mAnimationName;

    public static AnimationFragment newInstance(int animationName) {
        AnimationFragment fragment = new AnimationFragment();
        Bundle args = new Bundle();
        args.putInt(ANIMATION_NAME_PARAM, animationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnimationName = getArguments().getInt(ANIMATION_NAME_PARAM, AnimationNames.Spring);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_animation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FrameLayout layout = view.findViewById(R.id.animation_content);
        switch (mAnimationName) {
            case AnimationNames.Spring: {
                initSpringAnimation(layout);
                break;
            }
            case AnimationNames.Fling: {
                initFlingAnimation(layout);
                break;
            }
            case AnimationNames.Value: {
                initValueAnimation(layout);
                break;
            }
            case AnimationNames.Scale: {
                initScaleAnimation(layout);
                break;
            }
        }
    }

    private void initSpringAnimation(ViewGroup viewGroup) {
        SpringImageView view = new SpringImageView(getContext());
        view.setImageDrawable(getResources().getDrawable(R.drawable.banner));
        viewGroup.addView(view);
    }

    private void initFlingAnimation(ViewGroup viewGroup) {
        FlingImageView view = new FlingImageView(getContext());
        view.setImageDrawable(getResources().getDrawable(R.drawable.banner));
        viewGroup.addView(view);
    }

    private void initValueAnimation(ViewGroup viewGroup) {
        AnimationImageView view = new AnimationImageView(getContext());
        view.setImageDrawable(getResources().getDrawable(R.drawable.banner));
        viewGroup.addView(view);
    }

    private void initScaleAnimation(ViewGroup viewGroup) {
        ScaleImageView view = new ScaleImageView(getContext());
        view.setImageDrawable(getResources().getDrawable(R.drawable.banner));
        viewGroup.addView(view);
    }

    public interface AnimationNames {
        int Spring = 0;
        int Fling = 1;
        int Value = 2;
        int Scale = 3;
    }
}
