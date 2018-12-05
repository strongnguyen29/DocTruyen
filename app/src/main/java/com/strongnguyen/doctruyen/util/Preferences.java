package com.strongnguyen.doctruyen.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Mr Cuong on 1/19/2016.
 */
public class Preferences {

    private static SharedPreferences setting;
    private static SharedPreferences.Editor editor;

    public static String getString(Context context, String key, String defaultStr) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);

        return setting.getString(key, defaultStr);
    }

    public static int getInt(Context context, String key, int defaultInt) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        return setting.getInt(key, defaultInt);
    }

    public static boolean getBoolean(Context context, String key, boolean defaultBool) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);

        return setting.getBoolean(key, defaultBool);
    }

    public static void saveString(Context context, String key, String value) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        editor = setting.edit();
        editor.putString(key, value).apply();
    }

    public static void saveInt(Context context, String key, int value) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        editor = setting.edit();
        editor.putInt(key, value).apply();
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        setting = PreferenceManager.getDefaultSharedPreferences(context);
        editor = setting.edit();
        editor.putBoolean(key, value).apply();
    }

}
