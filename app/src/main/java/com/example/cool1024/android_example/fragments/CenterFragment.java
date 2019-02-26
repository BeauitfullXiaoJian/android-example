package com.example.cool1024.android_example.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import pub.devrel.easypermissions.EasyPermissions;

import com.example.cool1024.android_example.DevActivity;
import com.example.cool1024.android_example.GlideApp;
import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.fragments.FlvFragments.FlvFragment;
import com.example.cool1024.android_example.services.MusicService;
import com.example.cool1024.android_example.tasks.AppDownloadTask;

public class CenterFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "CenterFragment";
    private static final int APP_INSTALL_STORAGE_REQUEST_CODE = 1;
    private Activity mParentActivity;

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_center, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mParentActivity = getActivity();
        // 设置菜单点击事件
        int[] viewIds = {
                R.id.btn_banner,
                R.id.btn_draw,
                R.id.btn_flv,
                R.id.btn_camera,
                R.id.btn_music,
                R.id.btn_sofa,
                R.id.btn_spring,
                R.id.btn_fling,
                R.id.btn_animation,
                R.id.btn_scale
        };
        for (int id : viewIds) {
            view.findViewById(id).setOnClickListener(CenterFragment.this);
        }
        // 设置顶部背景图片
        final ImageView imageView = view.findViewById(R.id.toolbar_background);
        GlideApp.with(view).load(R.drawable.banner).into(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_spring: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, AnimationFragment.TAG);
                intent.putExtra(AnimationFragment.ANIMATION_NAME_PARAM, AnimationFragment.AnimationNames.Spring);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_fling: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, AnimationFragment.TAG);
                intent.putExtra(AnimationFragment.ANIMATION_NAME_PARAM, AnimationFragment.AnimationNames.Fling);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_animation: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, AnimationFragment.TAG);
                intent.putExtra(AnimationFragment.ANIMATION_NAME_PARAM, AnimationFragment.AnimationNames.Value);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_scale: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, AnimationFragment.TAG);
                intent.putExtra(AnimationFragment.ANIMATION_NAME_PARAM, AnimationFragment.AnimationNames.Scale);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_banner: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, BannerFragment.TAG);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_draw: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, DrawFragment.TAG);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_camera: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, CameraFragment.TAG);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_flv: {
                Intent intent = new Intent(getActivity(), DevActivity.class);
                intent.putExtra(DevActivity.FRAGMENT_NAME_PARAM, FlvFragment.TAG);
                mParentActivity.startActivity(intent);
                break;
            }
            case R.id.btn_music: {
                switch (MusicService.sPlayStatus) {
                    case MusicService.PlayStatus.EMPTY: {
                        MusicService.playMusic(getActivity(), "https://cool1024.com/upload/c2d8f23c236f257039305cc263ec6439.mp3");
                        break;
                    }
                    case MusicService.PlayStatus.PLAYING: {
                        MusicService.pauseMusic(getActivity());
                        break;
                    }
                    case MusicService.PlayStatus.PAUSE: {
                        MusicService.startMusic(getActivity());
                        break;
                    }
                }
                break;
            }
            case R.id.btn_sofa: {
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(mParentActivity, perms)) {
                    AppDownloadTask.setApkUrl("https://qd.myapp.com/myapp/qqteam/Androidlite/qqlite_3.7.1.704_android_r110206_GuanWang_537057973_release_10000484.apk");
                    AppDownloadTask.setTitle("应用更新");
                    AppDownloadTask.setCompleteListener((uri) -> showToast("应用下载完毕"));
                    AppDownloadTask task = AppDownloadTask.getInstance();
                    task.execute(getActivity());
                } else {
                    EasyPermissions.requestPermissions(this, "应用更新需要使用本地存储权限",
                            APP_INSTALL_STORAGE_REQUEST_CODE, perms);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadcastReceiver();
    }

    /**
     * 初始化广播接收器
     */
    private void initBroadcastReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mParentActivity);
        IntentFilter broadcastFilter = new IntentFilter();
        broadcastFilter.addAction(MusicService.TAG);
        localBroadcastManager.registerReceiver(new MusicBroadcastReceiver(), broadcastFilter);
    }

    class MusicBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showToast(intent.getStringExtra(MusicService.TAG));
            Log.d(TAG, "接收到广播消息");
        }
    }
}