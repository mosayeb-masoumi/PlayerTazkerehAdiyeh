package com.example.playertazkerehadiyeh.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.example.playertazkerehadiyeh.utils.Constant.SINGER_ADIYE;
import static com.example.playertazkerehadiyeh.utils.Constant.SONG_ADIYE;

public class SharedPrefManager {

    private static String SHOW_HELP = "ShowHelp";

    private static SharedPreferences SharedPrefManager(Context context){
        return context.getSharedPreferences("SharedPref", 0);
    }
    public static void SharedPrefManagerDelete(Context context){
        SharedPreferences preferences =context.getSharedPreferences("SharedPref",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }



    public static void setSingerAdiye(Context context,String chooser){
        SharedPrefManager(context).edit().putString(SINGER_ADIYE, chooser).apply();
    }
    public static String getSingerAdiye(Context context){
        return  SharedPrefManager(context).getString(SINGER_ADIYE,"");
    }


    public static void setSongAdiye(Context context,String chooser){
        SharedPrefManager(context).edit().putString(SONG_ADIYE, chooser).apply();
    }
    public static String getSongAdiye(Context context){
        return  SharedPrefManager(context).getString(SONG_ADIYE,"");
    }

}
