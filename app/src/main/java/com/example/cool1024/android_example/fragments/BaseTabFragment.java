package com.example.cool1024.android_example.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

public class BaseTabFragment extends Fragment implements RequestAsyncTask.ResponseCallback {

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

    @Override
    public void onError(String errorMsg) {
        this.showToast(errorMsg);
    }

    public void showToast(final String message) {
        final Activity activity = this.getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
