package com.example.cool1024.android_example.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import pub.devrel.easypermissions.EasyPermissions;

import android.util.Log;
import android.widget.Toast;

import com.example.cool1024.android_example.http.ApiData;
import com.example.cool1024.android_example.http.RequestAsyncTask;

public class BaseFragment extends Fragment implements RequestAsyncTask.ResponseCallback {

    private static final int MIN_TOAST_TIME = 3000;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
