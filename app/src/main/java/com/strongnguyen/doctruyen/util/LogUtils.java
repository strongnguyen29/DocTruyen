package com.strongnguyen.doctruyen.util;

import android.util.Log;

import com.strongnguyen.doctruyen.BuildConfig;


/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 8/23/2017.
 * Email: vancuong2941989@gmail.com
 */

public class LogUtils {

    public static void d(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.d(tag + "===|", msg + "");
    }

    public static void i(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.i(tag + "===|", msg + "");
    }

    public static void e(String tag, String msg) {
        if (!BuildConfig.DEBUG) return;
        Log.e(tag + "===|", msg + "");
    }
}
