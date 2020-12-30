package com.example.playertazkerehadiyeh.servive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.example.playertazkerehadiyeh.App;
import com.example.playertazkerehadiyeh.R;
import com.example.playertazkerehadiyeh.broadcast.CloseAdiehBroadcast;
import com.example.playertazkerehadiyeh.broadcast.ForwardAdiehBroadcast;
import com.example.playertazkerehadiyeh.broadcast.PauseAdiehBroadcast;
import com.example.playertazkerehadiyeh.broadcast.PlayAdiehBroadCast;
import com.example.playertazkerehadiyeh.broadcast.RewindAdiehBroadcast;
import com.example.playertazkerehadiyeh.model.CurrentPlayerTime;
import com.example.playertazkerehadiyeh.utils.ConverterEnDigitToFa;
import com.example.playertazkerehadiyeh.utils.SharedPrefManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class PlayerService extends Service {


    MediaPlayer mediaPlayer;

    String CHANNEL_ID = "channelId";
    NotificationManager notifManager;
    RemoteViews remoteViews;

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;


    public PlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show();

//        EventBus.getDefault().register(this);
//        mediaPlayer = MediaPlayer.create(this, R.raw.music);


        Uri uri1 = Uri.fromFile(new File(App.Adiye_path));
        mediaPlayer = MediaPlayer.create(this, uri1);
//        mediaPlayer.setLooping(false);


        callStateListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        String state = intent.getStringExtra("state");

        if(state.equals("play")){
            mediaPlayer.start();
            App.player_duration  = mediaPlayer.getDuration();
            updateSeekbar();

        }else if(state.equals("pause")){
            mediaPlayer.pause();
            App.player_duration  = mediaPlayer.getDuration();
            updateSeekbar();

        }

        else if(state.equals("forward")){
            long totleMilisecond = mediaPlayer.getDuration();
            long curreMilisecond = mediaPlayer.getCurrentPosition();
            long seekToForward = curreMilisecond + 5000;
            if(seekToForward < totleMilisecond){
                mediaPlayer.seekTo((int) seekToForward);
            }
        }

        else if(state.equals("rewind")){
//            long totleMilisecond = mediaPlayer.getDuration();
            long curreMilisecond = mediaPlayer.getCurrentPosition();
            long seekToRewind = curreMilisecond - 5000;
            if(seekToRewind > 0){
                mediaPlayer.seekTo((int) seekToRewind);
            }else{
                mediaPlayer.seekTo(0);
            }
        }

        else if(state.equals("seekbar_touch")){

            timerHandler.removeCallbacks(updater);  // to stop handler (sending current position every second)  to prevent seekbar jumping
            int playPosition = (int) ((mediaPlayer.getDuration() / 100)* App.seekbar_progress);
            mediaPlayer.seekTo(playPosition);
            App.player_progress = mediaPlayer.getCurrentPosition();

            updateSeekbar();
        }


        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            stopSelf();
            timerHandler.removeCallbacks(updater);  // to stop sending data every second
            EventBus.getDefault().post("stop");
        });





        remoteViews = new RemoteViews(this.getPackageName(), R.layout.notif_layout);
        setListener(remoteViews);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String offerChannelName = "Service Channel";
            String offerChannelDescription = "Music Channel";
            int offerChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, offerChannelName, offerChannelImportance);
            notifChannel.setDescription(offerChannelDescription);
            notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(notifChannel);
        }


        NotificationCompat.Builder sNotifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.play)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher_background))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteViews);
//                .setCustomBigContentView(remoteViews);
//                .setContent(remoteViews);  // show collaps notification



        Notification servNotification = sNotifBuilder.build();
//        servNotification.bigContentView = remoteViews;   // show expand notification
        startForeground(1, servNotification);


//        return START_REDELIVER_INTENT;  // good for play music or download a file  resume
        return START_NOT_STICKY;

    }


    Runnable updater;
    final Handler timerHandler = new Handler();

    private void updateSeekbar() {

        App.player_progress = mediaPlayer.getCurrentPosition();

        CurrentPlayerTime currentPlayerTime = new CurrentPlayerTime();
        currentPlayerTime.setCurrentTime(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
        long currentPosition = mediaPlayer.getCurrentPosition();
        String duartion = milliSecondToTimer(mediaPlayer.getDuration());
        currentPlayerTime.setProgress(currentPosition);
        currentPlayerTime.setTotal(duartion);
        currentPlayerTime.setDuration(mediaPlayer.getDuration());
        EventBus.getDefault().post(currentPlayerTime);

// run after second and periodically
        updater = new Runnable() {
            @Override
            public void run() {

                App.player_progress = mediaPlayer.getCurrentPosition();
                timerHandler.postDelayed(updater,1000);

                CurrentPlayerTime currentPlayerTime = new CurrentPlayerTime();
                currentPlayerTime.setCurrentTime(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
                long currentPosition = mediaPlayer.getCurrentPosition();
                String duartion = milliSecondToTimer(mediaPlayer.getDuration());
                currentPlayerTime.setProgress(currentPosition);
                currentPlayerTime.setTotal(duartion);
                currentPlayerTime.setDuration(mediaPlayer.getDuration());
                EventBus.getDefault().post(currentPlayerTime);
            }
        };
        timerHandler.post(updater);

    }

    private void setListener(RemoteViews remoteViews) {

        Intent intentPlay = new Intent(this, PlayAdiehBroadCast.class);
        intentPlay.putExtra("state", "play");
        PendingIntent pendingPlay = PendingIntent.getBroadcast(this, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_play, pendingPlay);


        Intent intentPause = new Intent(this, PauseAdiehBroadcast.class);
        intentPause.putExtra("state", "pause");
        PendingIntent pendingPause = PendingIntent.getBroadcast(this, 0, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_pause, pendingPause);


        Intent intentForward = new Intent(this, ForwardAdiehBroadcast.class);
        intentForward.putExtra("state", "forward");
        PendingIntent pendingForward = PendingIntent.getBroadcast(this, 0, intentForward, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_forward, pendingForward);

        Intent intentRewind = new Intent(this, RewindAdiehBroadcast.class);
        intentRewind.putExtra("state", "rewind");
        PendingIntent pendingRewind = PendingIntent.getBroadcast(this, 0, intentRewind, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_rewind, pendingRewind);

        Intent intentClose = new Intent(this, CloseAdiehBroadcast.class);
        intentClose.putExtra("state", "close");
        PendingIntent pendingClose = PendingIntent.getBroadcast(this, 0, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.img_close, pendingClose);





        remoteViews.setTextViewText(R.id.txt_title_notif_adiye , SharedPrefManager.getSongAdiye(PlayerService.this));
        remoteViews.setTextViewText(R.id.txt_singer_notif_adiye , SharedPrefManager.getSingerAdiye(PlayerService.this));

        if(mediaPlayer.isPlaying()){
            remoteViews.setViewVisibility(R.id.img_pause, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.img_play, View.GONE);
        }else{
            remoteViews.setViewVisibility(R.id.img_play, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.img_pause, View.GONE);
        }
    }




    @Override
    public void onDestroy() {

        mediaPlayer.stop();
        stopSelf();
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        EventBus.getDefault().post("stop");
        timerHandler.removeCallbacks(updater);  // to stop handler (sending current position every second)

    }




    private String milliSecondToTimer(long milliSeconds) {
//
        String timerString = "";
        String secondsString;
        int hour = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
//
        if (hour > 0) {
            timerString = ConverterEnDigitToFa.convert(String.valueOf(hour))+ ":";
        }

        if (seconds < 10) {
            secondsString = "۰" +ConverterEnDigitToFa.convert(String.valueOf(seconds)) ;
        } else {
            secondsString = "" + ConverterEnDigitToFa.convert(String.valueOf(seconds)) ;
        }

        timerString = timerString +ConverterEnDigitToFa.convert(String.valueOf(minutes))  + ":" + secondsString;
        return timerString;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }






    //کنترل تماس های ورودی
    //تابع بعدی که میخواهیم داشته باشیم برای جلوگیری از پخش موسیقی در هنگام زنگ خوردن موبایل است
    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }




}