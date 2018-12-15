package com.example.cool1024.android_example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.cool1024.android_example.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends BaseTabFragment {

    public static final String TAG = "DashboardFragment";

    private WebView mWebView;

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        findViewComponent(view);
        initView();
        onHiddenChanged(false);
        return view;
    }

    private void findViewComponent(View view) {
        mProgressBar = view.findViewById(R.id.load_bar);
        mWebView = view.findViewById(R.id.web_view);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void initView() {
        // android 8.0 版本无法加载chart.js, e_charts.js(百度图表)
        // mWebView.loadUrl("file:///android_asset/dist/index.html");
        mWebView.loadUrl("https://blog.cool1024.com");
    }
}
