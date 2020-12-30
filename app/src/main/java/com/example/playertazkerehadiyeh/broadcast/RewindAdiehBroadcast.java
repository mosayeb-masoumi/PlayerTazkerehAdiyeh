package com.example.playertazkerehadiyeh.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import com.example.playertazkerehadiyeh.servive.PlayerService;
import com.example.playertazkerehadiyeh.utils.ConverterEnDigitToFa;

public class RewindAdiehBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() != null) {

            String flag = intent.getStringExtra("state");
            Toast.makeText(context, ConverterEnDigitToFa.convert("5") + " ثانیه به عقب", Toast.LENGTH_SHORT).show();
            if (flag.equals("rewind")) {

                Intent intent2 = new Intent(context, PlayerService.class);
                intent2.putExtra("state","rewind");
                ContextCompat.startForegroundService(context, intent2);
            }
        }
    }
}
