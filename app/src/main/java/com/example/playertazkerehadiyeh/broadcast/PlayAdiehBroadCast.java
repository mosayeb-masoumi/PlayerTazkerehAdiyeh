package com.example.playertazkerehadiyeh.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import com.example.playertazkerehadiyeh.servive.PlayerService;

import org.greenrobot.eventbus.EventBus;

public class PlayAdiehBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() != null) {
            String flag = intent.getStringExtra("state");
            if (flag.equals("play")) {

                Intent intent2 = new Intent(context, PlayerService.class);
                intent2.putExtra("state","play");
                ContextCompat.startForegroundService(context, intent2);
                EventBus.getDefault().postSticky("play");
            }
        }
    }
}
