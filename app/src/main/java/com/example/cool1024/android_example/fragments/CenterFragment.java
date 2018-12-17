package com.example.cool1024.android_example.fragments;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cool1024.android_example.FragmentDevActivity;
import com.example.cool1024.android_example.MainActivity;
import com.example.cool1024.android_example.PhotoViewActivity;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.SettingsActivity;
import com.example.cool1024.android_example.UserInfoActivity;
import com.example.cool1024.android_example.services.DownloadService;
import com.example.cool1024.android_example.services.MusicService;


/**
 * A simple {@link Fragment} subclass.
 */
public class CenterFragment extends BaseTabFragment implements ServiceConnection,
        DownloadService.ProgressListener, ViewTreeObserver.OnScrollChangedListener {

    public static final String TAG = "CenterFragment";

    public static final String CHANNEL_ID = "CenterFragment";

    public static final Integer REQUEST_STORAGE_WRITE = 1;

    public static final Integer INSTALL_PACKAGES_REQUEST_CODE = 2;

    private Integer mNotifyId = 0;

    private AppCompatActivity mParentActivity;
    private Toolbar mToolbar;
    private ImageView mBackgroundImageView;
    private NestedScrollView mScrollView;

    private NotificationManager mNotificationManager;

    private Uri mApkUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_center, container, false);
        setHasOptionsMenu(true);
        findViewComponent(view);
        initView();
        initViewEvent(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "添加菜单项");
        inflater.inflate(R.menu.center, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.center_menu_item_settings: {
                startActivity(new Intent(mParentActivity, UserInfoActivity.class));
            }
        }
        return true;
    }

    /**
     * 视图发生滚动执行方法
     */
    @Override
    public void onScrollChanged() {
        int offset = mScrollView.getTop() + mScrollView.getScrollY();
        int maxOffset = 700;
        float ratio = (float) Math.min(offset, maxOffset) / maxOffset;
        mToolbar.getBackground().setAlpha((int) (255 * ratio));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(offset >= maxOffset ? 26 : 0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果是来自第三方包安装授权，那么执行安装请求（安装请求中会校验是否授权了）
        if (requestCode == INSTALL_PACKAGES_REQUEST_CODE) {
            requestApkInstall();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "接收到请求回调");

        // 存储卡写入权限
        if (requestCode == REQUEST_STORAGE_WRITE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "成功授权，可以写入本地文件");
                Intent intent = new Intent(getActivity(), DownloadService.class);
                intent.putExtra(DownloadService.DOWNLOAD_URL_KEY,
                        "https://www.cool1024.com/app-debug.apk");
                mParentActivity.bindService(intent, CenterFragment.this, Context.BIND_AUTO_CREATE);
            } else {
                Log.d(TAG, "拒绝授权，无法写入本地文件");
                Toast.makeText(getContext(), "拒绝授权,无法下载文件", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        // 第三方应用安装权限
        if (requestCode == INSTALL_PACKAGES_REQUEST_CODE) {
            Log.d(TAG, "长度" + grantResults.length);
            if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "成功授权，允许安装第三方应用");
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                installIntent.setDataAndType(mApkUri, "application/vnd.android.package-archive");
                startActivity(installIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "拒绝授权，不允许安装第三方应用");
                Toast.makeText(getContext(), "拒绝授权,无法安装最新版本应用", Toast.LENGTH_SHORT)
                        .show();
                // 跳转到允许设置页面
                Uri packageURI = Uri.parse("package:" + mParentActivity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                startActivityForResult(intent, INSTALL_PACKAGES_REQUEST_CODE);
            }
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

    public void onInstall(Uri apkUri) {
        Log.d(TAG, "请求安装应用");
        mApkUri = apkUri;
        Log.d(TAG, apkUri.getPath());
        requestApkInstall();
    }

    /**
     * 找到视图中的相关组件
     *
     * @param view 当前视图
     */
    private void findViewComponent(View view) {
        Context context = getContext();
        mParentActivity = (AppCompatActivity) getActivity();
        mNotificationManager = context != null ? (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE) : null;
        mBackgroundImageView = view.findViewById(R.id.image_bg);
        mToolbar = view.findViewById(R.id.center_toolbar);
        mScrollView = view.findViewById(R.id.scroll_view);
    }

    /**
     * 初始化视图组件相关事件
     *
     * @param view 当前视图
     */
    private void initViewEvent(View view) {
        view.findViewById(R.id.settings_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParentActivity.startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        view.findViewById(R.id.notify_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotifyMessage("通知标题" + mNotifyId, "测试发送通知相关内容" + mNotifyId);
            }
        });
        view.findViewById(R.id.download_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 请求存储卡写入权限
                requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_WRITE);
                Log.d(TAG, "请求本地存储授权");
            }
        });
        view.findViewById(R.id.media_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MusicService.class);
                mParentActivity.startService(intent);
            }
        });
        view.findViewById(R.id.btn_draw_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FragmentDevActivity.class);
                intent.putExtra(FragmentDevActivity.FRAGMENT_NAME_PARAM, ImageDrawFragment.TAG);
                mParentActivity.startActivity(intent);
            }
        });
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(CenterFragment.this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        Glide.with(CenterFragment.this)
                .load("https://hello1024.oss-cn-beijing.aliyuncs.com/upload/banner/201808310312505b88b23288693.jpg")
                .into(mBackgroundImageView);
        mToolbar.getBackground().setAlpha(0);
        mParentActivity.setSupportActionBar(mToolbar);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Download task");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(channel);
        }

        // 创建一个NotificationCompat.Builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mParentActivity,
                CHANNEL_ID)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(PendingIntent.getActivity(getActivity(),
                        0, intent, 0));

        // 显示通知
        mNotificationManager.notify(++mNotifyId, mBuilder.build());
    }

    /**
     * 请求安装应用授权
     */
    private void requestApkInstall() {

        // 判断是否允许安装第三方来源apk--26版本以上需要请求权限
        Log.d(TAG, "SDK_INIT:" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            PackageManager packageManager = mParentActivity.getPackageManager();

            // 如果不允许安装，跳转到开启授权页面
            if (!packageManager.canRequestPackageInstalls()) {
                Log.d(TAG, "请求安装未知应用来源的权限");
                requestPermissions(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUEST_CODE);
            } else {
                // 手动通知授权成功
                onRequestPermissionsResult(INSTALL_PACKAGES_REQUEST_CODE,
                        new String[]{},
                        new int[]{PackageManager.PERMISSION_GRANTED});
            }

        } else {
            onRequestPermissionsResult(INSTALL_PACKAGES_REQUEST_CODE,
                    new String[]{},
                    new int[]{PackageManager.PERMISSION_GRANTED});
        }
    }
}