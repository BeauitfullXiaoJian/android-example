package com.example.cool1024.android_example.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Locale;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MusicService extends IntentService {

    public static final String TAG = "MusicService";
    private static final String MUSIC_URL = "MUSIC_URL";
    private static final String ACTION_PLAY = "ACTION_PLAY";
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static int sPlayStatus = PlayStatus.EMPTY;

    private MediaPlayer mMediaPlayer;
    private LocalBroadcastManager mLocalBroadcastManager;

    public interface PlayStatus {
        int EMPTY = 0;
        int LOADING = 1;
        int PLAYING = 2;
        int PAUSE = 3;
    }

    public MusicService() {
        super("MusicService");
    }

    public static void playMusic(Context context, String musicUrl) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(MUSIC_URL, musicUrl);
        context.startService(intent);
    }

    public static void startMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void pauseMusic(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PAUSE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                final String musicUrl = intent.getStringExtra(MUSIC_URL);
                handleActionPlay(musicUrl);
            } else if (ACTION_START.equals(action)) {
                handleActionStart();
            } else if (ACTION_PAUSE.equals(action)) {
                handleActionPause();
            }
        }
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
            mMediaPlayer = null;
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
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        sPlayStatus = PlayStatus.EMPTY;
    }
}
