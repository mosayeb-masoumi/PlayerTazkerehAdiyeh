package com.example.playertazkerehadiyeh;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

public class App extends Application {

    public static Context myContext;
    public static String path_save_aud="";

    public static String Adiye_path="";
    public static long player_duration =0;
    public static long player_progress =0;
    public static long seekbar_progress =0;


    @Override
    public void onCreate() {
        super.onCreate();

        myContext= this;

        // create folder (where our song will saved)
        path_save_aud=  Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + myContext.getResources().getString(R.string.app_name)+ File.separator+"audio";

        final File newFile2 = new File(path_save_aud);
        newFile2.mkdir();
        newFile2.mkdirs();
    }
}
