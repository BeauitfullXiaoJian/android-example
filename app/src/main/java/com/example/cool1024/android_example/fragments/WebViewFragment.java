package com.example.cool1024.android_example.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
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
import com.example.cool1024.android_example.http.Pagination;

public class WebViewFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "WebViewFragment";

    private WebView mWebView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        findViewComponent(view);
        initView();
    }

    private void findViewComponent(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.web_swipe);
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
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setProgress(newProgress);
                if (newProgress >= 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(WebViewFragment.this);
        mSwipeRefreshLayout.setOnChildScrollUpCallback((p, c) -> mWebView.getScrollY() > 0);
    }

    private void initView() {
        // android 8.0 版本无法加载chart.js, e_charts.js(百度图表)
        // mWebView.loadUrl("file:///android_asset/dist/index.html");
        mWebView.loadUrl("https://blog.cool1024.com");
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    @Override
    public void onRefresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        mWebView.loadUrl("https://blog.cool1024.com");
    }
}
