package com.example.playertazkerehadiyeh.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.example.playertazkerehadiyeh.App;
import com.example.playertazkerehadiyeh.model.AdiehVoiceModel;
import com.example.playertazkerehadiyeh.R;
import com.example.playertazkerehadiyeh.model.CurrentPlayerTime;
import com.example.playertazkerehadiyeh.servive.PlayerService;
import com.example.playertazkerehadiyeh.utils.CircularSeekBar;
import com.example.playertazkerehadiyeh.utils.ConverterEnDigitToFa;
import com.example.playertazkerehadiyeh.utils.SharedPrefManager;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {


    CircularSeekBar seekBar;
    ImageView img_play, img_pause;

    String voiceTitle;
    String singer;
    String url;
    String endpoint;

    TextView textTotalDuration;
    TextView textCurrentTime;
    TextView txt_percent;
    SeekBar playerSeekbar;

    RelativeLayout rl_player;

    AVLoadingIndicatorView avi_downlaod;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        initView();
        
        List<AdiehVoiceModel> voiceList = new ArrayList<>();
        voiceList.add(new AdiehVoiceModel("https://api.tazkereh.app/media/ZiarateAminollah-Karimi.mp3", "زیارت امین الله", "کریمی", "ZiarateAminollah-Karimi.mp3", 2));
        voiceList.add(new AdiehVoiceModel("https://api.tazkereh.app/media/Komeil-Karimi.mp3", "دعای کمیل", "کریمی", "Komeil-Karimi.mp3", 7));
        voiceList.add(new AdiehVoiceModel("https://api.tazkereh.app/media/Tavasol-Karimi.mp3", "دعای توسل", "کریمی", "Tavasol-Karimi.mp3", 8));
        voiceList.add(new AdiehVoiceModel("https://api.tazkereh.app/media/Nodbe-Karimi.mp3", "دعای ندبه", "کریمی", "Nodbe-Karimi.mp3", 9));
        voiceList.add(new AdiehVoiceModel("https://api.tazkereh.app/media/Ahd-Farahmand.mp3", "دعای عهد", "فرهمند", "Ahd-Farahmand.mp3", 12));
        voiceList.add(new AdiehVoiceModel("https://api.tazkereh.app/media/Faraj-Karimi.mp3", "دعای فرج", "کریمی", "Faraj-Karimi.mp3", 13));




        for (int i = 0; i < voiceList.size(); i++) {

            if (i == 4) {
                voiceTitle = voiceList.get(i).getTitle();
                singer = voiceList.get(i).getSinger();
                url = voiceList.get(i).getUrl();
                endpoint = voiceList.get(i).getEndpoint();

                SharedPrefManager.setSingerAdiye(MainActivity.this, singer);
                SharedPrefManager.setSongAdiye(MainActivity.this, voiceTitle);

            }
        }


        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);


    }

    private void initView() {

        seekBar = findViewById(R.id.circularSeekBar2);
        txt_percent = findViewById(R.id.txt_percent);
        img_play = findViewById(R.id.img_play);
        img_pause = findViewById(R.id.img_pause);
        textTotalDuration = findViewById(R.id.textTotalDuration);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        playerSeekbar = findViewById(R.id.playerSeekbar);

        avi_downlaod = findViewById(R.id.avi_downlaod);

        rl_player = findViewById(R.id.rl_player);


        rl_player.setVisibility(GONE);
        img_play.setOnClickListener(view -> {
            playMusic();
        });

        img_pause.setOnClickListener(view -> {
            imgPlayVisible();
            Intent intent = new Intent(MainActivity.this, PlayerService.class);
            intent.putExtra("state", "pause");
            ContextCompat.startForegroundService(MainActivity.this, intent);
        });





        // for single click and slide click
        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    App.seekbar_progress = progress;
                    long seekBarProgress = progress;
                    Intent intent = new Intent(MainActivity.this, PlayerService.class);
                    intent.putExtra("state", "seekbar_touch");
                    intent.putExtra("seekbar_progress", seekBarProgress);
                    ContextCompat.startForegroundService(MainActivity.this, intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void imgPauseVisible() {
        img_pause.setVisibility(VISIBLE);
        img_play.setVisibility(GONE);
//        seekBar.setVisibility(VISIBLE);
//        seekBar.setProgress(0);
    }

    private void imgPlayVisible() {
        img_play.setVisibility(VISIBLE);
        img_pause.setVisibility(GONE);
//        seekBar.setProgress(0);
//        seekBar.setVisibility(GONE);
//        App.player_progress=0;
//        App.player_duration=0;

    }

    private void playMusic() {

        if (checkWriteExternalPermission()) {

            if (musicDownloaded()) {
                String path = App.path_save_aud + "/" + endpoint;
                startPlaying(path);
                imgPauseVisible();
            } else {

                if(checkInternetConnection()){
                    downloadVoice();
                }else{
                    Toast.makeText(this, "لطفا اتصال به اینترنت را چک کنید", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            askExternalStoragePermission();
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null;
    }

    private boolean musicDownloaded() {

        String path = App.path_save_aud;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
//        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {


            if (files[i].getName().equals(endpoint)) {

                App.Adiye_path = files[i].getAbsolutePath();

                return true;
            }
        }

        return false;
    }


    private void downloadVoice() {

        avi_downlaod.setVisibility(VISIBLE);
        img_play.setEnabled(false);
        PRDownloader.download(url, App.path_save_aud, endpoint)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        avi_downlaod.setVisibility(GONE);
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        avi_downlaod.setVisibility(GONE);
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Log.i("TAG", "onDownloadComplete: ");
                        avi_downlaod.setVisibility(GONE);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        avi_downlaod.setVisibility(GONE);
                        txt_percent.setVisibility(View.VISIBLE);

                        long divide = progress.totalBytes / 100;
                        long percent = (int) progress.currentBytes / divide;
                        txt_percent.setText(ConverterEnDigitToFa.convert(String.valueOf(percent)) + "%");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        avi_downlaod.setVisibility(GONE);
                        img_play.setEnabled(true);
                        imgPauseVisible();
                        txt_percent.setVisibility(View.GONE);
                        Log.i("TAG", "onDownloadComplete: ");

                        // play automatically after download
                        String path = App.path_save_aud + "/" + endpoint;
                        startPlaying(path);


                    }

                    @Override
                    public void onError(Error error) {
                        img_play.setEnabled(true);
                        avi_downlaod.setVisibility(GONE);
                        txt_percent.setVisibility(GONE);
                        Toast.makeText(MainActivity.this, "خطای در دانلود, مجددا تلاش کنید", Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "onDownloadComplete: ");
                    }
                });


    }


    private void startPlaying(String path) {

        rl_player.setVisibility(VISIBLE);
        App.Adiye_path = path;
        Intent intent = new Intent(MainActivity.this, PlayerService.class);
        intent.putExtra("state", "play");
        ContextCompat.startForegroundService(MainActivity.this, intent);
    }


    private void askExternalStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
    }


    private boolean checkWriteExternalPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String state) {
        Log.i("TAG", "onEventMainThread: ");

        if (state.equals("pause")) {
            imgPlayVisible();
        } else if (state.equals("play")) {
            imgPauseVisible();
        } else if (state.equals("stop")) {
            playerSeekbar.setProgress(0);
            textCurrentTime.setText(ConverterEnDigitToFa.convert("0:00"));
            imgPlayVisible();
            rl_player.setVisibility(GONE);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(CurrentPlayerTime model) {
        Log.i("TAG", "onEventMainThread: ");

        textCurrentTime.setText(model.getCurrentTime());
        textTotalDuration.setText(model.getTotal());

        playerSeekbar.setProgress((int) (((float) model.getProgress() / model.getDuration()) * 100));
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}