package com.codinginflow.siring.adzan;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.codinginflow.siring.R;

public class MusikService extends Service {
    MediaPlayer mp;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }
    @Override
    public void onCreate(){
        mp= MediaPlayer.create(this, R.raw.adzan);
        mp.setLooping(false);
    }

    public void onStart(Intent intent, int startid){
        mp.start();
    }
    public void onDestroy(){
        mp.stop();
    }
}
