package com.strongnguyen.doctruyen;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.strongnguyen.doctruyen.data.TruyenFullParser;
import com.strongnguyen.doctruyen.model.ContentChap;
import com.strongnguyen.doctruyen.util.LogUtils;

import java.util.Locale;

public class ReaderService extends Service {
    private static final String TAG = "ReaderService";

    private IBinder binder;
    private OnReaderServiceListener onReaderListener;
    private TextToSpeech textToSpeech;

    private ContentChap mContentChap;
    private int currentSpeech;
    private String[] arrTextSpeech;
    private String bookUrl;

    @Override
    public void onCreate() {
        Log.d("ReaderService", "Đã gọi onCreate()");

        binder = new ReaderBinder();
        setupTextToSpeech();
        super.onCreate();
    }

    // Bắt đầu một Service
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");

        return binder;
    }

    // Kết thúc một Service
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ServiceDemo", "Đã gọi onBind()");
        return super.onUnbind(intent);
    }

    public void setOnReaderListener(OnReaderServiceListener onReaderListener) {
        this.onReaderListener = onReaderListener;
    }

    public void nextChapter() {
        currentSpeech = 0;
        loadContenChap(mContentChap.getNextUrl());
    }

    private void loadContenChap(String url) {
        mContentChap = TruyenFullParser.getInstance().getContentChap(url);
        String text = mContentChap.getContent();
        arrTextSpeech = text.split("\n");
        speakOut();
    }

    /**
     * Setup text to speech;
     */
    public void setupTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("vi_VN"));
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        LogUtils.e("TTS", "This Language is not supported");
                    } else {
                        LogUtils.d(TAG, "TTS : This Language is supported");
                        //btnReading.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                LogUtils.d(TAG, "onStart: " + s);
            }

            @Override
            public void onDone(String s) {
                LogUtils.d(TAG, "onDone: " + s);
                currentSpeech = Integer.parseInt(s);

                if (currentSpeech == arrTextSpeech.length - 1) {
                    // het chuong se tai chuong tiep theo.
                    nextChapter();
                }
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                LogUtils.d(TAG, "onStop: utteranceId = " + utteranceId + " | interrupted = " + interrupted);
            }

            @Override
            public void onError(String s) {
                LogUtils.d(TAG, "onError: " + s);
            }
        });
        textToSpeech.setPitch(0.8f);
        textToSpeech.setSpeechRate(1.60f);
    }

    /**
     * Chay TTS;
     */
    private void speakOut() {
        textToSpeech.speak(arrTextSpeech[currentSpeech], TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentSpeech));

        int i = currentSpeech + 1;
        while (i < arrTextSpeech.length) {
            textToSpeech.speak(arrTextSpeech[i], TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
            i++;
        }
    }

    public class ReaderBinder extends Binder {
        // phương thức này trả về đối tượng MyService
        public ReaderService getService() {
            return ReaderService.this;
        }
    }

    public interface OnReaderServiceListener {

        void onLoadContentChap(ContentChap contentChap);
    }
}
