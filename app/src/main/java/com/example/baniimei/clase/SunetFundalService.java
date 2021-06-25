package com.example.baniimei.clase;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.baniimei.R;

public class SunetFundalService extends Service {

    MediaPlayer mediaPlayer;

    //metoda obligatorie clasa abstracta Service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        // Setam reluarea melodiei:
        mediaPlayer.setLooping(true);
        // volum mic pt ca este de fundal
        mediaPlayer.setVolume(30, 30);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.start();
        //Toast.makeText(getApplicationContext(), "Playing Bohemian Rashpody in the Background",    Toast.LENGTH_SHORT).show();
        return startId;
    }

    //public void onStart(Intent intent, int startId) { }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
    }
}
