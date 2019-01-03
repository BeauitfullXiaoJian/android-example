package com.example.cool1024.android_example.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.cool1024.android_example.R;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * B站播放器演示.
 */
public class FlvFragment extends Fragment
        implements SurfaceHolder.Callback, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public final static String TAG = "FlvFragmentLog";
    private Activity mParentActivity;
    private IjkMediaPlayer mIjkMediaPlayer;
    private SurfaceView mPlayView;
    private SeekBar mSeekBar;
    private PlayListener mPlayListener = new PlayListener();

    private void preparePlayer() {
        mIjkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mIjkMediaPlayer.setOnPreparedListener(mPlayListener);
        mIjkMediaPlayer.setOnSeekCompleteListener(mPlayListener);
        // mIjkMediaPlayer.setOnBufferingUpdateListener(mPlayListener);
        // 开启硬解码
        // mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        try {
            mIjkMediaPlayer.setDataSource("http://192.168.1.117/live.flv");
            mIjkMediaPlayer.setDisplay(mPlayView.getHolder());
            mIjkMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "视频取流失败");
        }
    }

    /**
     * 更新视频进度状态
     */
    private void updatePlaySeek() {
        Log.d(TAG, "视频当前位置" + mIjkMediaPlayer.getCurrentPosition());
        Log.d(TAG, "视频缓冲进度" + mIjkMediaPlayer.getVideoCachedDuration());
        Log.d(TAG, "视频播放进度" + mIjkMediaPlayer.getDuration());
        mSeekBar.setMax((int) mIjkMediaPlayer.getDuration());
        mSeekBar.setProgress((int) mIjkMediaPlayer.getCurrentPosition());
        mSeekBar.setSecondaryProgress((int) mIjkMediaPlayer.getCurrentPosition() + (int) mIjkMediaPlayer.getVideoCachedDuration());
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "进度条被修改" + progress);
        mIjkMediaPlayer.seekTo(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_flv, container, false);
        mainView.findViewById(R.id.log_btn).setOnClickListener(FlvFragment.this);
        mParentActivity = getActivity();
        mSeekBar = mainView.findViewById(R.id.play_progress);
        mSeekBar.setOnSeekBarChangeListener(FlvFragment.this);
        mPlayView = mainView.findViewById(R.id.play_view);
        mPlayView.getHolder().addCallback(FlvFragment.this);
        return mainView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        preparePlayer();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    class PlayListener implements IMediaPlayer.OnPreparedListener,
            IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "视频准备就绪");
            iMediaPlayer.start();
            updatePlaySeek();
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "视频跳转成功");
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            Log.d(TAG, "缓冲更新");
//            mParentActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    logPlayStatus();
//                }
//            });
        }
    }
}
