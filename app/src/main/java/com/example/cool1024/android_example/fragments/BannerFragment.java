package com.example.cool1024.android_example.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cool1024.android_example.GlideApp;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.components.TouchImageView;

public class BannerFragment extends BaseFragment {

    public static final String TAG = "BannerFragmentLog";
    private static final String IMAGE_URLS_PARAM = "IMAGE_URLS_PARAM";

    private String[] mImageUrls;

    public static BannerFragment newInstance(String[] imageUrls) {
        BannerFragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        args.putStringArray(IMAGE_URLS_PARAM, imageUrls);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrls = getArguments().getStringArray(IMAGE_URLS_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated((savedInstanceState));
        View mainView = getView();
        Activity activity = getActivity();
        if (mainView != null) {
            ViewPager viewPager = mainView.findViewById(R.id.view_pager);
            viewPager.setAdapter(new BannerPagerAdapter());
            viewPager.addOnPageChangeListener(new PagerChangeListener());
        }
        if (activity != null) {
            // 设置全屏模式
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if (activity != null) {
            // 设置默模式
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    class PagerChangeListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            Log.d(TAG, "移动" + position + "偏移坐标" + positionOffset);
            if (position == (mImageUrls.length - 1)) {
                showToast("已经是最后一页了");
            }
        }
    }

    class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageUrls.length;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Context context = getContext();
            TouchImageView imageView = new TouchImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(2000, 2000));
            AnimationDrawable animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.bg_loading);
            animationDrawable.start();
            GlideApp.with(container)
                    .load(mImageUrls[position])
                    .placeholder(animationDrawable)
                    .into(imageView);
            container.addView(imageView);
            Log.d(TAG, "实例化" + position);
            return imageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}