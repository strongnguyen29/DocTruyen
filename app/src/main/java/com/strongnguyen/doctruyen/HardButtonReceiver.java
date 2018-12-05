package com.strongnguyen.doctruyen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.strongnguyen.doctruyen.util.LogUtils;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/27/2018.
 * Email: vancuong2941989@gmail.com
 */
public class HardButtonReceiver extends BroadcastReceiver {

    private final static String TAG = "HardButtonReceiver";

    private HardButtonListener mButtonListener;

    public HardButtonReceiver(HardButtonListener buttonListener) {
        super();
        mButtonListener = buttonListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "HardButtonReceiver: Button press received");
        if(mButtonListener != null) {
            /**
             * We abort the broadcast to prevent the event being passed down
             * to other apps (i.e. the Music app)
             */
            abortBroadcast();

            // Pull out the KeyEvent from the intent
            KeyEvent key = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            // This is just some example logic, you may want to change this for different behaviour
            if(key.getAction() == KeyEvent.ACTION_UP) {
                int keycode = key.getKeyCode();
                LogUtils.d(TAG, "keyCode = " + keycode);
                // These are examples for detecting key presses on a Nexus One headset
                if(keycode == KeyEvent.KEYCODE_HEADSETHOOK) {
                    mButtonListener.onPlayPauseButtonPress();
                }
            }
        }
    }

    public interface HardButtonListener {
        void onPlayPauseButtonPress();
    }
}
