package com.example.cool1024.android_example.fragments;


import android.Manifest;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cool1024.android_example.MainActivity;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.SettingsActivity;
import com.example.cool1024.android_example.UserInfoActivity;
import com.example.cool1024.android_example.services.DownloadService;
import com.example.cool1024.android_example.services.MusicService;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CenterFragment extends Fragment implements ServiceConnection, DownloadService.ProgressListener {

    public static final String TAG = "CenterFragment";

    public static final String CHANNEL_ID = "CenterFragment";

    public static final Integer INSTALL_PACKAGES_REQUEST_CODE = 1;

    public static final Integer REQUEST_STORAGE_WRITE = 1;

    private Integer mNotifyId = 0;

    private ImageView mBackgroundImageView;

    private NotificationManagerCompat mNotificationManagerCompat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_center, container, false);
        findViewComponent(view);
        initView(view);
        initViewEvent(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            if (getActivity().getPackageManager().canRequestPackageInstalls()) {
                Log.d(TAG, "同意了安装第三方应用的请求，开始安装应用程序");
            } else {
                Log.d(TAG, "拒绝了安装第三方应用的请求，无法安装应用程序");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG,"接收到请求回调");
        if(requestCode == REQUEST_STORAGE_WRITE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"成功授权，可以写入本地文件");
            Intent intent = new Intent(getActivity(), DownloadService.class);
            intent.putExtra(DownloadService.DOWNLOAD_URL_KEY,
                    "https://cool1024.com/upload/47e0b428f30fde9a0395b18e6db62ddd.mp4");
            getActivity().bindService(intent, CenterFragment.this, Context.BIND_AUTO_CREATE);
        }else{
            Log.d(TAG,"拒绝授权，无法写入本地文件");
            Toast.makeText(getContext(),"拒绝授权,无法下载文件",Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
        binder.getService().setProgressListener(CenterFragment.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public void onProgress(DownloadService.DownloadData downloadData) {
        Log.d(TAG, downloadData.toString());
    }

    private void findViewComponent(View view) {
        mNotificationManagerCompat = NotificationManagerCompat.from(getActivity());
        mBackgroundImageView = (ImageView) view.findViewById(R.id.image_bg);
    }

    private void initView(View view) {
        Glide.with(CenterFragment.this)
                .load("https://hello1024.oss-cn-beijing.aliyuncs.com/upload/banner/201808310313195b88b24fe6db3.png")
                .into(mBackgroundImageView);
    }

    private void initViewEvent(View view) {
        view.findViewById(R.id.settings_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        view.findViewById(R.id.image_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), UserInfoActivity.class));
            }
        });
        view.findViewById(R.id.notify_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotifyMessage("通知标题", "测试发送通知相关内容");
            }
        });
        view.findViewById(R.id.download_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 请求存储卡写入权限
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_WRITE);
                Log.d(TAG,"请求本地存储授权");
            }
        });
        view.findViewById(R.id.media_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicService.class);
                getActivity().startService(intent);
            }
        });
    }

    /**
     * 发送通知消息
     *
     * @param title   通知标题
     * @param message 通知内容
     */
    private void showNotifyMessage(String title, String message) {

        // 我们需要点击通知打开一个Activity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // 创建一个NotificationCompat.Builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(getActivity(),
                        0, intent, 0));

        // 显示通知
        mNotificationManagerCompat.notify(++mNotifyId, mBuilder.build());
    }

    /**
     * 请求安装应用授权
     */
    private void requestApkInstall() {

        // 判断是否允许安装第三方来源apk--26版本以上需要请求权限
        Log.d(TAG, "SDK_INIT:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            PackageManager packageManager = getActivity().getPackageManager();

            // 如果不允许安装，跳转到开启授权页面
            if (!packageManager.canRequestPackageInstalls()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, INSTALL_PACKAGES_REQUEST_CODE);
            }
        } else {
            onRequestPermissionsResult(INSTALL_PACKAGES_REQUEST_CODE,
                    new String[]{},
                    new int[]{PackageManager.PERMISSION_GRANTED});
        }
    }

    /**
     * 安装apk文件
     *
     * @param apkFile apk文件对象
     */
    private void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = null;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            apkUri = Uri.fromFile(apkFile);

        } else {
            // Android7.0之后获取uri要用contentProvider
            apkUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName(), apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}