package com.example.playertazkerehadiyeh.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.playertazkerehadiyeh.servive.PlayerService;

import org.greenrobot.eventbus.EventBus;

public class CloseAdiehBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getExtras() != null) {
            context.stopService(new Intent(context , PlayerService.class));
            EventBus.getDefault().postSticky("stop");
        }

    }
}
