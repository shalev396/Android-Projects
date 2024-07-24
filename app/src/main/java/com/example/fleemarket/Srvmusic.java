package com.example.fleemarket;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class Srvmusic extends Service {
    protected MediaPlayer mp;
    protected boolean isPlaying = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isPlaying) {
            mp = MediaPlayer.create(getApplicationContext(), R.raw.back1);
            mp.setLooping(true);
            mp.start();
            isPlaying = true;
        }
        else
            stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (isPlaying) {
            mp.stop();
            mp.release();
            isPlaying = false;
        }
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}