package com.example.cool1024.android_example.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MusicService extends Service {

    public static final String TAG = "MusicService";

    public static int sPlayStatus = PlayStatus.EMPTY;
    private MediaPlayer mMediaPlayer;
    private LocalBroadcastManager mLocalBroadcastManager;
    private final MusicBinder mMusicBinder = new MusicBinder();

    public interface PlayStatus {
        int EMPTY = 0;
        int LOADING = 1;
        int PLAYING = 2;
        int PAUSE = 3;
    }

    public class MusicBinder extends Binder {
        public void playMusic(String musicUrl) {
            handleActionPlay(musicUrl);
        }

        public void startMusic() {
            Log.d(TAG, "播放音乐");
            handleActionStart();
        }

        public void pauseMusic() {
            Log.d(TAG, "暂停音乐");
            handleActionPause();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    private void handleActionPlay(String musicUrl) {
        cleanMediaPlayer();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(musicUrl);
            mMediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
                String msg = String.format(Locale.CHINA, "缓冲进度:%d%c", percent, '%');
                sendOutMessage(msg);
            });
            mMediaPlayer.setOnCompletionListener(mp -> {
                sendOutMessage("播放结束");
                cleanMediaPlayer();
            });
            mMediaPlayer.setOnPreparedListener((mp) -> {
                sendOutMessage("开始播放音乐");
                mMediaPlayer.start();
                sPlayStatus = PlayStatus.PLAYING;
            });
            mMediaPlayer.prepare();
            sPlayStatus = PlayStatus.LOADING;
            sendOutMessage("成功创建音乐播放组件");
        } catch (Exception e) {
            sendOutMessage("音频播放失败，请检查您的网络");
            e.printStackTrace();
        }
    }

    private void handleActionStart() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            sPlayStatus = PlayStatus.PLAYING;
        }
    }

    private void handleActionPause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            sPlayStatus = PlayStatus.PAUSE;
        }
    }

    private void sendOutMessage(String msg) {
        Log.d(TAG, msg);
        if (mLocalBroadcastManager == null) {
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(MusicService.this);
        }
        Intent intent = new Intent(MusicService.TAG);
        intent.putExtra(MusicService.TAG, msg);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private void cleanMediaPlayer() {
        Log.d(TAG, "我被人销毁了！！！！！");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        sPlayStatus = PlayStatus.EMPTY;
    }
}
