package com.example.cool1024.android_example.fragments.FlvFragments;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.cool1024.android_example.R;
import com.example.cool1024.android_example.classes.DMManager;
import com.example.cool1024.android_example.classes.FlvDetail;
import com.example.cool1024.android_example.classes.FragmentPage;
import com.example.cool1024.android_example.classes.ViewDisappearHandler;
import com.example.cool1024.android_example.fragments.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import master.flame.danmaku.controller.IDanmakuView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * B站播放器演示.
 */
public class FlvFragment extends BaseFragment implements
        SurfaceHolder.Callback,
        View.OnClickListener,
        View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "FlvFragmentLog";
    private static final String FLV_DETAIL = "FLV_DETAIL";

    private AppCompatActivity mParentActivity;
    private IDanmakuView mMessageView;
    private IjkMediaPlayer mIjkMediaPlayer;
    private SurfaceView mPlayView;
    private SeekBar mSeekBar;
    private ProgressBar mLoadingBar;
    private RelativeLayout mPlayPad;
    private ImageView mPlayBtn;
    private ImageView mDanBtn;
    private TextView mPlayTime;
    private EditText mMsgInput;

    // 弹幕视图管理器
    private DMManager mDMManager;
    // 当前的播放状态
    private int mPlayStatus = PlayStatus.PLAYING;
    // 当前是否在跳转
    private Boolean mIsSeeking = Boolean.FALSE;
    // 播放器快照状态
    private int mPlaySnapshotStatus = PlayStatus.PLAYING;
    // 视频相关数据
    private FlvDetail mFlvDetail;

    private ViewDisappearHandler mControlDisappearHandler;
    private GestureDetector mGestureDetector;
    private PlayListener mPlayListener = new PlayListener();


    /**
     * 创建一个相册列表Fragment
     *
     * @param flvDetail 视频相关信息
     * @return FlvFragment
     */
    public static FlvFragment newInstance(FlvDetail flvDetail) {
        FlvFragment fragment = new FlvFragment();
        Bundle args = new Bundle();
        args.putSerializable(FLV_DETAIL, flvDetail);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 准备播放器
     */
    private void preparePlayer() {
        // 如果播放器之前已经创建，那么只需要恢复即可
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.setDisplay(mPlayView.getHolder());
            recoverPlaySnapshot();
            return;
        }

        // 如果没有播放器，创建播放器
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        mIjkMediaPlayer = new IjkMediaPlayer();
        mIjkMediaPlayer.setOnPreparedListener(mPlayListener);
        mIjkMediaPlayer.setOnSeekCompleteListener(mPlayListener);
        mIjkMediaPlayer.setOnErrorListener(mPlayListener);
        mIjkMediaPlayer.setOnInfoListener(mPlayListener);
        mIjkMediaPlayer.setOnBufferingUpdateListener(mPlayListener);
        mIjkMediaPlayer.setSpeed(1);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        // mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration",
        //        60 * 60);
        // mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        // mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        // IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        try {
            mIjkMediaPlayer.setDataSource(mFlvDetail.getFlvUrl());
            mIjkMediaPlayer.setDisplay(mPlayView.getHolder());
            mIjkMediaPlayer.prepareAsync();
            setLoadingStatus();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "视频取流失败");
        }
    }

    /**
     * 启动播放器
     */
    private void startPlayer() {
        Log.d(TAG, "恢复播放器");
        if (mIjkMediaPlayer != null) {
            setPlayingStatus();
            mIjkMediaPlayer.start();
            mMessageView.seekTo(mMessageView.getCurrentTime());
        }
    }

    /**
     * 暂停播放器
     */
    private void pausePlayer() {
        Log.d(TAG, "暂停播放器");
        if (mIjkMediaPlayer != null) {
            setPauseStatus();
            mIjkMediaPlayer.pause();
            // mMessageView.pause();
        }
    }

    /**
     * 销毁播放器
     */
    private void destroyPlayer() {
        Log.d(TAG, "销毁播放器");
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.release();
        }
    }

    /**
     * 准备弹幕播放器
     */
    private void prepareMessageView() {
        InputStream inputStream = getResources().openRawResource(R.raw.comments);
        mMessageView.prepare(mDMManager.createParser(inputStream), mDMManager.getDMContext());
        // mMessageView.showFPS(true);
        mMessageView.enableDanmakuDrawingCache(true);
    }

    /**
     * 设置当前为加载状态
     */
    private void setLoadingStatus() {
        mParentActivity.runOnUiThread(() -> mLoadingBar.setVisibility(View.VISIBLE));
    }

    /**
     * 设置当前为就绪状态
     */
    private void setPreparedStatus() {
        mParentActivity.runOnUiThread(() ->
                mLoadingBar.setVisibility(View.INVISIBLE));
    }

    /**
     * 设置当前为播放状态
     */
    private void setPlayingStatus() {
        mPlayStatus = PlayStatus.PLAYING;
        mParentActivity.runOnUiThread(() ->
                mPlayBtn.setImageDrawable(mParentActivity.getDrawable(R.drawable.ic_play_stop)));
    }

    /**
     * 设置当前为暂停状态
     */
    private void setPauseStatus() {
        mPlayStatus = PlayStatus.PAUSED;
        mParentActivity.runOnUiThread(() ->
                mPlayBtn.setImageDrawable(mParentActivity.getDrawable(R.drawable.ic_play)));
    }

    /**
     * 保存播放器快照状态
     */
    private void savePlaySnapshot() {
        if (mIjkMediaPlayer != null) {
            mPlaySnapshotStatus = mPlayStatus;
            mIjkMediaPlayer.pause();
            Log.d(TAG, "保存快照数据，当前播放状态:" + mPlayStatus);
        }
    }

    /**
     * 恢复播放器快照状态
     */
    private void recoverPlaySnapshot() {
        if (mPlaySnapshotStatus == PlayStatus.PAUSED) {
            setPauseStatus();
            mIjkMediaPlayer.pause();
        } else {
            setPlayingStatus();
            mIjkMediaPlayer.start();
        }
    }

    /**
     * 设置全屏状态
     */
    private void setFullScreen() {
        savePlaySnapshot();
        View decorView = mParentActivity.getWindow().getDecorView();
        Display display = mParentActivity.getWindowManager().getDefaultDisplay();
        Point sizePoint = new Point();
        display.getRealSize(sizePoint);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mDanBtn.setVisibility(View.VISIBLE);
        mParentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mParentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayPad.getLayoutParams();
        params.width = Math.max(sizePoint.x, sizePoint.y);
        params.height = Math.min(sizePoint.x, sizePoint.y);
        mPlayPad.setLayoutParams(params);
        // 设置播放器边距
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayView.getLayoutParams();
        layoutParams.width = params.width;
        layoutParams.height = (int) (params.width / (16.0 / 9));
        // 如果高度不够，那么缩小
        if (layoutParams.height > params.height) {
            layoutParams.width = (int) (params.height * (16.0 / 9));
            layoutParams.height = params.height;
            int margin = (params.width - layoutParams.width) / 2;
            layoutParams.leftMargin = margin;
            layoutParams.rightMargin = margin;
        } else {
            int margin = (params.height - layoutParams.height) / 2;
            layoutParams.topMargin = margin;
            layoutParams.bottomMargin = margin;
        }
        mPlayView.setLayoutParams(layoutParams);
        mMsgInput.setVisibility(View.GONE);
    }

    /**
     * 设置默认（竖屏）状态
     */
    private void setDefaultScreen() {
        savePlaySnapshot();
        mDanBtn.setVisibility(View.GONE);
        mParentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mParentActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPlayPad.getLayoutParams();
        params.width = Math.min(displaymetrics.widthPixels, displaymetrics.heightPixels);
        params.height = (int) (params.width / (16.0 / 9));
        mPlayPad.setLayoutParams(params);
        // 设置播放器边距
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayView.getLayoutParams();
        layoutParams.width = params.width;
        layoutParams.height = params.height;
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        mPlayView.setLayoutParams(layoutParams);
        mMsgInput.setVisibility(View.GONE);
    }

    private void autoScreen() {
        // 获取当前是横屏还是竖屏
        Configuration configuration = getResources().getConfiguration();
        // 根据横竖屏调整布局大小
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreen();
            Log.d(TAG, "全屏");
        } else {
            setDefaultScreen();
            Log.d(TAG, "默认");
        }
    }

    /**
     * 更新播放进度条
     */
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

    /**
     * 格式化播放时间
     *
     * @param duration 毫秒时长
     * @return 00:00格式的时长
     */
    private String formatDuration(long duration) {
        duration = duration / 1000;
        int minute = (int) (duration / 60);
        int second = (int) (duration % 60);
        return String.format("%s:%s", minute > 9 ? minute :
                "0" + minute, second > 9 ? second : "0" + second);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFlvDetail = (FlvDetail) (getArguments().getSerializable(FLV_DETAIL));
        }
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
        mDanBtn = mainView.findViewById(R.id.btn_danmaku);
        mPlayTime = mainView.findViewById(R.id.play_time);
        mSeekBar.setOnSeekBarChangeListener(FlvFragment.this);
        mPlayPad = mainView.findViewById(R.id.play_pad);
        mPlayView = mainView.findViewById(R.id.play_view);
        mPlayView.getHolder().addCallback(FlvFragment.this);
        mMessageView = mainView.findViewById(R.id.message_view);
        mMsgInput = mainView.findViewById(R.id.msg_input);
        mPlayView.setOnTouchListener(FlvFragment.this);
        mControlDisappearHandler = new ViewDisappearHandler(mainView.findViewById(R.id.play_control));
        mGestureDetector = new GestureDetector(mParentActivity, new GestureListener());

        // 调整全屏/默认
        autoScreen();
        // setDefaultScreen();
        FragmentManager fragmentManager = getChildFragmentManager();
        ViewPager viewPager = mainView.findViewById(R.id.view_pager);
        viewPager.setAdapter(new FlvFragmentPagerAdapter(fragmentManager));
        viewPager.setCurrentItem(0);
        viewPager.clearOnPageChangeListeners();
        TabLayout tabLayout = mainView.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        mControlDisappearHandler.hideView(5000);
        mDMManager = new DMManager(mMessageView);
        prepareMessageView();
        return mainView;
    }

    @Override
    public void onPause() {
        super.onPause();
        pausePlayer();
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
            case R.id.btn_danmaku:{
                mMessageView.start();
                break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean result = Boolean.FALSE;
        v.performClick();
        switch (v.getId()) {
            case R.id.play_view: {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mControlDisappearHandler.showView();
                }
                if (action == MotionEvent.ACTION_UP) {
                    mControlDisappearHandler.hideView(5000);
                }
                result = mGestureDetector.onTouchEvent(event);
            }
        }
        return result;
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

    }

    final class PlayStatus {
        static final int PLAYING = 1;
        static final int PAUSED = 2;
    }

    class PlayListener implements IMediaPlayer.OnPreparedListener,
            IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnBufferingUpdateListener,
            IMediaPlayer.OnErrorListener, IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "视频准备就绪");
            iMediaPlayer.start();
            updatePlaySeek();
            setPreparedStatus();
            setPlayingStatus();
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "播放已经结束");
            setPauseStatus();
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            Log.d(TAG, "INFO" + what);
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                setLoadingStatus();
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                setPreparedStatus();
            }
            return false;
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Log.d(TAG, "视频跳转成功");
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
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                // 如果当前是竖屏，那么转为横屏（全屏）
                setFullScreen();
            } else {
                // 如果是横屏，那么转为竖屏（默认）
                setDefaultScreen();
            }
            return true;
        }
    }

    class FlvFragmentPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<FragmentPage> pages;

        FlvFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            pages = new ArrayList<>();
            pages.add(new FragmentPage(FlvDetailFragment.newInstance(mFlvDetail.getId()), "详情"));
            pages.add(new FragmentPage(FlvCommentFragment.newInstance(mFlvDetail.getId()), "评论"));
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @NonNull
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
