package com.ami.com.ami.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import sim.ami.com.myapplication.R;

/**
 * Created by hi on 5/11/16.
 */
public class MyPreference {
    private static MyPreference instance;
    private static Context context;
    public MyPreference(Context context) {
        this.context = context;

    }
    public static MyPreference getInstance(Context context){
        if(instance == null){
            instance = new MyPreference(context);
        }
        return instance;
    }
    public Config getConfig(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String resolution = sharedPreferences.getString(getStringFromId(R.string.pref_resolution),"1");
        String frameRate = sharedPreferences.getString(getStringFromId(R.string.pref_frame_rate),"4");
        String bitRate = sharedPreferences.getString(getStringFromId(R.string.pref_bit_rate),"1");
        String orientation = sharedPreferences.getString(getStringFromId(R.string.pref_orientation),"1");
        boolean isEnableRecordAudion = sharedPreferences.getBoolean(getStringFromId(R.string.pref_record_audio_enable),false);
        boolean isEnableShowCam = sharedPreferences.getBoolean(getStringFromId(R.string.pref_camera_show_enable),false);
        boolean isEnableAutoRecord = sharedPreferences.getBoolean(getStringFromId(R.string.pref_auto_record_enable),true);
        boolean isEnableShowTouches = sharedPreferences.getBoolean(getStringFromId(R.string.pref_show_touches_enable),false);
        boolean isEnableShowCountDown = sharedPreferences.getBoolean(getStringFromId(R.string.pref_countdown_enable),false);
        String countDownValue = sharedPreferences.getString(getStringFromId(R.string.pref_counddown_value),"3");

        Config config = new Config(resolution,frameRate,bitRate,orientation,isEnableRecordAudion,isEnableShowCam,isEnableAutoRecord,isEnableShowTouches,isEnableShowCountDown,countDownValue);
        Log.e("Preference",config.toString());
        return config;
    }
    private String getStringFromId(int id){
        String base = context.getString(id);
        return base;
    }

    public void setTutorialPassed(boolean passed) {
        SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPred.edit();
        editor.putBoolean(getStringFromId(R.string.pref_tutorial_passed), passed);
        editor.commit();
    }

    public boolean isTutorialPassed () {
        SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(context);
        boolean passed = sharedPred.getBoolean(getStringFromId(R.string.pref_tutorial_passed), false);
        return passed;
    }
}
