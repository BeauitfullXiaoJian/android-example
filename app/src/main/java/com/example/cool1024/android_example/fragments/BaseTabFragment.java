package com.example.cool1024.android_example.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

public class BaseTabFragment extends Fragment implements RequestAsyncTask.ResponseCallback {

    public static final int MIN_TOAST_TIME = 3000;
    private static long sToastShowTime = 0;
    public static final String EMPTY_TAG = "NONE";

    public String getFragmentTag() {
        return EMPTY_TAG;
    }

    @Override
    public void onResponse(ApiData apiData) {
    }

    @Override
    public void onComplete() {

    }

    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onError(String errorMsg) {
        this.showToast(errorMsg);
    }

    public void showToast(final String message) {
        final Activity activity = this.getActivity();
        if (activity != null && System.currentTimeMillis() - sToastShowTime > MIN_TOAST_TIME) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
            sToastShowTime = System.currentTimeMillis();
        }
    }
}
