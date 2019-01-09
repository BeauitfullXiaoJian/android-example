package com.example.cool1024.android_example.fragments.FlvFragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.FragmentPage;
import com.example.cool1024.android_example.fragments.BaseTabFragment;

import java.io.IOException;
import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * B站播放器演示.
 */
public class FlvFragment extends BaseTabFragment implements
        SurfaceHolder.Callback,
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener {

    public final static String TAG = "FlvFragmentLog";

    private AppCompatActivity mParentActivity;
    private IjkMediaPlayer mIjkMediaPlayer;
    private SurfaceView mPlayView;
    private SeekBar mSeekBar;
    private ProgressBar mLoadingBar;
    private RelativeLayout mPlayPad;
    private ImageView mPlayBtn;
    private TextView mPlayTime;

    private int mPlayStatus = 0;
    private GestureDetector gestureDetector;
    private Boolean mIsSeeking = false;

    private PlayListener mPlayListener = new PlayListener();

    private void preparePlayer() {
        setLoadingStatus();
        mIjkMediaPlayer = new IjkMediaPlayer();
        mIjkMediaPlayer.setOnPreparedListener(mPlayListener);
        mIjkMediaPlayer.setOnSeekCompleteListener(mPlayListener);
        mIjkMediaPlayer.setOnErrorListener(mPlayListener);
        mIjkMediaPlayer.setOnBufferingUpdateListener(mPlayListener);
        // mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        try {
            mIjkMediaPlayer.setDataSource("http://192.168.0.103:8080/html/live.flv");
            mIjkMediaPlayer.setDisplay(mPlayView.getHolder());
            mIjkMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "视频取流失败");
        }
    }

    private void startPlayer() {
        Log.d(TAG, "恢复播放器");
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.start();
            setPlayingStatus();
        }
    }

    private void pausePlayer() {
        Log.d(TAG, "暂停播放器");
        if (mIjkMediaPlayer != null) {
            setPauseStatus();
            mIjkMediaPlayer.pause();
        }
    }

    private void destroyPlayer() {
        Log.d(TAG, "销毁播放器");
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.release();
        }
    }

    private void setPlayingStatus() {
        mPlayStatus = PlayStatus.PLAYING;
        mParentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingBar.setVisibility(View.INVISIBLE);
                mPlayBtn.setImageDrawable(mParentActivity.getDrawable(R.drawable.ic_pause_black_24dp));
            }
        });
    }

    private void setSeekCompleteStatus() {
        if (mIjkMediaPlayer.isPlaying()) {
            setPlayingStatus();
        } else {
            setPauseStatus();
        }
    }

    private void setLoadingStatus() {
        mPlayStatus = PlayStatus.LOADING;
        mParentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setPauseStatus() {
        mPlayStatus = PlayStatus.PAUSED;
        mParentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingBar.setVisibility(View.INVISIBLE);
                mPlayBtn.setImageDrawable(mParentActivity.getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
        });
    }

    private void setFullScreen() {
        View decorView = mParentActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        mParentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mParentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayPad.getLayoutParams();
        params.height = displaymetrics.heightPixels;
        mPlayPad.setLayoutParams(params);
    }

    private void setDefaultScreen() {
        View decorView = mParentActivity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        mParentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mParentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayPad.getLayoutParams();
        params.height = (int) (displaymetrics.widthPixels / (16.0 / 9));
        mPlayPad.setLayoutParams(params);
    }

    @Override
    public boolean onBackPressed() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 如果当前是横屏，那么退出横屏（全屏）
            setDefaultScreen();
            return Boolean.FALSE;
        } else {
            // 如果当前是竖屏，那么退出Activity
            return Boolean.TRUE;
        }
    }

    private void updatePlaySeek() {
        // Log.d(TAG, "视频当前位置" + mIjkMediaPlayer.getCurrentPosition());
        // Log.d(TAG, "视频缓冲进度" + mIjkMediaPlayer.getVideoCachedDuration());
        // Log.d(TAG, "视频播总长度" + mIjkMediaPlayer.getDuration());
        long duration = mIjkMediaPlayer.getDuration();
        long position = mIjkMediaPlayer.getCurrentPosition();
        long cache = mIjkMediaPlayer.getVideoCachedDuration();
        String playTime = String.format("%s:%s",
                formatDuration(position), formatDuration(duration));
        mPlayTime.setText(playTime);
        mSeekBar.setMax((int) duration);
        mSeekBar.setProgress((int) position);
        mSeekBar.setSecondaryProgress((int) (position + cache));

    }

    private String formatDuration(long duration) {
        duration = duration / 1000;
        int minute = (int) (duration / 60);
        int second = (int) (duration % 60);
        return String.format("%s:%s", minute > 9 ? minute :
                "0" + minute, second > 9 ? second : "0" + second);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_flv, container, false);
        mainView.findViewById(R.id.btn_play).setOnClickListener(FlvFragment.this);
        mParentActivity = (AppCompatActivity) getActivity();
        mSeekBar = mainView.findViewById(R.id.play_progress);
        mLoadingBar = mainView.findViewById(R.id.play_loading);
        mPlayBtn = mainView.findViewById(R.id.btn_play);
        mPlayTime = mainView.findViewById(R.id.play_time);
        mSeekBar.setOnSeekBarChangeListener(FlvFragment.this);
        mPlayPad = mainView.findViewById(R.id.play_pad);
        mPlayView = mainView.findViewById(R.id.play_view);
        mPlayView.getHolder().addCallback(FlvFragment.this);
        mPlayView.setOnTouchListener(FlvFragment.this);
        gestureDetector = new GestureDetector(mParentActivity, new GestureListener());
        setDefaultScreen();
        FragmentManager fragmentManager = mParentActivity.getSupportFragmentManager();
        TabLayout tabLayout = mainView.findViewById(R.id.tab_layout);
        ViewPager viewPager = mainView.findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new FlvFragmentPagerAdapter(fragmentManager));
        viewPager.setCurrentItem(0);
        return mainView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play: {
                Log.d(TAG, "按下播放键" + mPlayStatus);
                if (mPlayStatus == PlayStatus.PLAYING) {
                    pausePlayer();
                } else if (mPlayStatus == PlayStatus.PAUSED) {
                    startPlayer();
                }
                break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsSeeking = true;
        Log.d(TAG, "锁定进度条更新");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mIsSeeking == Boolean.TRUE) {
            Log.d(TAG, "跳转到指定进度" + seekBar.getProgress());
            mIjkMediaPlayer.seekTo(seekBar.getProgress());
            setLoadingStatus();
        }
        Log.d(TAG, "解锁进度条更新");
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
        destroyPlayer();
    }

    final class PlayStatus {
        static final int LOADING = 0;
        static final int PLAYING = 1;
        static final int PAUSED = 2;
    }

    class PlayListener implements IMediaPlayer.OnPreparedListener,
            IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener,
            IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "视频准备就绪");
            iMediaPlayer.start();
            updatePlaySeek();
            setPlayingStatus();
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "播放已经结束");
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "视频跳转成功");
            setSeekCompleteStatus();
            mIsSeeking = false;
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            // Log.d(TAG, "缓冲更新");
            if (mIsSeeking == Boolean.FALSE) {
                updatePlaySeek();
            }
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            Log.d(TAG, "视频播放错误");
            return false;
        }
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "双击事件");
            setFullScreen();
            return true;
        }
    }

    class FlvFragmentPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<FragmentPage> pages;

        FlvFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            pages = new ArrayList<>();
            pages.add(new FragmentPage(FlvDetailFragment.newInstance(1), "详情"));
            pages.add(new FragmentPage(FlvCommentFragment.newInstance(1), "评论"));
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public Fragment getItem(int position) {
            return pages.get(position).getFragment();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return pages.get(position).getTitle();
        }
    }
}
