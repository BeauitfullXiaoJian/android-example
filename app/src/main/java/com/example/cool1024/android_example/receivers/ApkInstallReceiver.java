package com.example.cool1024.android_example.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ApkInstallReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiverLog";

    private NotificationManager mNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case Intent.ACTION_PACKAGE_ADDED: {
                    Log.d(TAG, "成功安装APP-新添加");
                    cleanUpdate();
                    break;
                }
                case Intent.ACTION_PACKAGE_REPLACED: {
                    Log.d(TAG, "成功替换APP-覆盖安装");
                    cleanUpdate();
                    break;
                }
            }
        }
    }

    private void cleanUpdate() {
        mNotificationManager.cancelAll();
    }
}
