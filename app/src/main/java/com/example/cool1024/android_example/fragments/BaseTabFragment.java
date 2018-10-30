package com.example.cool1024.android_example.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class BaseTabFragment extends Fragment implements Response.ErrorListener {

    public static final String EMPTY_TAG = "NONE";

    public String getFragmentTag() {
        return EMPTY_TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Activity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity.getApplicationContext(), "网络请求错误", Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
