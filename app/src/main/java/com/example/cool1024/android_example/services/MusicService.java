package com.example.cool1024.android_example.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service {

    public static final String TAG = "MusicService";

    private MediaPlayer mMediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mMediaPlayer == null) {
            Toast.makeText(this,"成功创建音乐播放组件",Toast.LENGTH_SHORT).show();
            mMediaPlayer = new MediaPlayer();
            try{
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource("https://cool1024.com/upload/c2d8f23c236f257039305cc263ec6439.mp3");
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.prepareAsync();
            }catch (Exception  e){
                Toast.makeText(this,"音频播放失败，请检查您的网络",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"成功启动服务，开始播放音乐",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"开始销毁服务，音乐播放停止",Toast.LENGTH_SHORT).show();
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }
}
