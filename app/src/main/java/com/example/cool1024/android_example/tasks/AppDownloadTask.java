package com.example.cool1024.android_example.tasks;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppDownloadTask extends AsyncTask<Context, Integer, Boolean> {

    private static final String TAG = "AppDownloadTask";
    private static AppDownloadTask sInstance;
    private static DownloadManager sDownloadManager;
    private static DownloadCompleteListener sListener;
    private static String sApkDownloadUrl;
    private static String sDownloadTitle;
    private static long sDownloadId;
    private static Uri sApkUri;
    private static DownloadManager.Request sDownloadRequest;

    public static AppDownloadTask startTask(Context context) {
        if (sInstance == null) {
            sInstance = new AppDownloadTask();
            sInstance.execute(context);
        }
        return sInstance;
    }

    public static Uri getApkUri() {
        return sApkUri;
    }

    public static void setApkUrl(String url) {
        sApkDownloadUrl = url;
    }

    public static void setTitle(String title) {
        sDownloadTitle = title;
        if (sDownloadRequest != null) {
            sDownloadRequest.setTitle(sDownloadTitle);
        }
    }

    public static void setCompleteListener(DownloadCompleteListener listener) {
        sListener = listener;
    }

    @Override
    protected Boolean doInBackground(Context... contexts) {
        boolean result = false;
        if (contexts.length > 0 && contexts[0] != null) {
            result = startDownloadRequest(contexts[0]);
        }
        if (result) {
            registerCompleteBroadcastReceiver(contexts[0]);
        }
        return result;
    }

    private Boolean startDownloadRequest(Context context) {
        boolean result = false;
        sDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (sDownloadManager != null) {
            // 创建下载请求
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(sApkDownloadUrl));
            // 设置通知栏可见
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 设置漫游下禁止下载
            request.setAllowedOverRoaming(false);
            // 允许下载的文件被系统Download应用管理
            request.setVisibleInDownloadsUi(true);
            // 根据日期生成下载文件保存名称
            String apkFileName = new SimpleDateFormat("yyyyMMdd-HH-mm-ss", Locale.getDefault())
                    .format(new Date()) + ".apk";
            // 设置文件保存地址
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName);
            // 设置下载标题
            request.setTitle(sDownloadTitle);
            // setting mime type
            request.setMimeType("application/vnd.android.package-archive");
            sDownloadId = sDownloadManager.enqueue(request);
            sDownloadRequest = request;
            result = true;
        } else {
            Log.d(TAG, "获取下载管理器失败～");
        }
        return result;
    }

    private void registerCompleteBroadcastReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                receiverDownloadCompleteMessage(intent);
            }
        }, intentFilter);
    }

    private void receiverDownloadCompleteMessage(Intent intent) {
        Log.d(TAG, "接收到广播消息");
        String action = intent.getAction();
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (action == null) {
            return;
        }
        Log.d(TAG, "下载编号" + id);
        if (id < 0 || id != sDownloadId) {
            return;
        }

        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE) && sListener != null) {
            Log.d(TAG, "执行完成方法");
            sApkUri = sDownloadManager.getUriForDownloadedFile(sDownloadId);
            sListener.onComplete(sApkUri);
        }
    }

    public interface DownloadCompleteListener {
        void onComplete(Uri uri);
    }
}
