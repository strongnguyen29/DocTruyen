package com.strongnguyen.doctruyen.ui;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.ui.popup.PopupGotoChap;
import com.strongnguyen.doctruyen.util.LogUtils;
import com.strongnguyen.doctruyen.util.Preferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 6/7/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ReaderLocalActivity extends AppCompatActivity {
    private static final String TAG = ReaderLocalActivity.class.getSimpleName();
    public static final String PREF_LOCAL_CHAP_NUM = "PREF_LOCAL_CHAP_NUM";
    public static final String PREF_LOCAL_CURRENT_SPEECH = "PREF_CURRENT_SPEECH";

    private TelephonyManager mgr;
    private PhoneStateListener phoneStateListener;

    private TextToSpeech textToSpeech;

    private ScrollView scrollViewContent;
    private TextView tvTitle;
    private TextView tvContent;
    private Button btnReading, btnNext, btnPrev, btnGotoChap;

    private PopupGotoChap popupGotoChap;

    private String[] arrTextBook;
    private String[] arrTextSpeech;

    private int currentChapNum;
    private int currentSpeech;

    boolean isSpeech = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader_local);

        currentChapNum = Preferences.getInt(getApplicationContext(), PREF_LOCAL_CHAP_NUM, 0);
        currentSpeech = Preferences.getInt(getApplicationContext(), PREF_LOCAL_CURRENT_SPEECH, 0);

        bindView();
        setupTextToSpeech();
        phoneStateEvent();

        try{
            LogUtils.d(TAG, "START load text");
            InputStream inputStream = getAssets().open("DDTLT.txt");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String textBook = new String(buffer);
            arrTextBook = textBook.split("Chính văn đệ");
            LogUtils.d(TAG, "END load text");
            setContentChapter();
        } catch (IOException e) {
            e.printStackTrace();
            tvContent.setText("Không tải được nội dung!!!");
        }
    }

    @Override
    protected void onDestroy() {
        if (popupGotoChap != null) {
            popupGotoChap.dismiss();
            popupGotoChap = null;
        }

        saveData();
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        textToSpeech.stop();
        textToSpeech.shutdown();

        super.onDestroy();
    }

    /**
     * Bind view;
     */
    public void bindView() {
        scrollViewContent = findViewById(R.id.scrollview_content);
        tvContent = findViewById(R.id.tv_content);

        tvTitle = findViewById(R.id.tv_title);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextChapter();
            }
        });
        btnPrev = findViewById(R.id.btn_prev);
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevChapter();
            }
        });
        btnReading = findViewById(R.id.btn_reading );
        btnReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSpeech) {
                    speakOut();
                    isSpeech = true;
                    btnReading.setText("Dừng");
                } else {
                    textToSpeech.stop();
                    isSpeech = false;
                    btnReading.setText("Đọc");
                    saveData();
                }
            }
        });
        btnGotoChap = findViewById(R.id.btn_chapter);
        btnGotoChap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupGotoChap == null) {
                    popupGotoChap = new PopupGotoChap(ReaderLocalActivity.this, new PopupGotoChap.OnPopupListener() {
                        @Override
                        public void onGoToChap(int chapnum) {
                            if (chapnum < arrTextBook.length) {
                                currentChapNum = chapnum;
                                setContentChapter();
                                currentSpeech = 0;
                                scrollViewContent.scrollTo(0,0);

                                if (isSpeech) {
                                    speakOut();
                                }
                            }
                        }
                    });
                }
                popupGotoChap.show();
            }
        });
    }
    Handler handler;
    private void setContentChapter() {
        if (arrTextBook[currentChapNum] != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTitle.setText("Chương " + currentChapNum);
                    tvContent.setText(arrTextBook[currentChapNum]);
                }
            });
            arrTextSpeech = arrTextBook[currentChapNum].split("\n");
        }
    }

    private void nextChapter() {
        LogUtils.d(TAG, "nextChapter");
        if (currentChapNum < arrTextBook.length) {
            currentChapNum++;
            setContentChapter();
            currentSpeech = 0;
            scrollViewContent.scrollTo(0,0);

            if (isSpeech) {
                speakOut();
            }
        }
    }

    private void prevChapter() {
        if (currentChapNum > 0) {
            currentChapNum--;
            setContentChapter();
            currentSpeech = 0;
            scrollViewContent.scrollTo(0,0);

            if (isSpeech) {
                speakOut();
            }
        }
    }

    /**
     * Doc text
     */
    private void speakOut() {
        textToSpeech.speak(arrTextSpeech[currentSpeech], TextToSpeech.QUEUE_FLUSH, null, String.valueOf(currentSpeech));

        int i = currentSpeech + 1;
        while (i < arrTextSpeech.length) {
            textToSpeech.speak(arrTextSpeech[i], TextToSpeech.QUEUE_ADD, null, String.valueOf(i));
            i++;
        }
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
                        btnReading.setEnabled(true);
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
        textToSpeech.setSpeechRate(1.55f);
    }

    /**
     * Phone state event;
     */
    private void phoneStateEvent() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music
                    LogUtils.d(TAG, "onCallStateChanged()");
                    isSpeech = false;
                    textToSpeech.stop();
                    btnReading.setText("Đọc");
                    saveData();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };

        mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    /**
     * Save data current reading;
     */
    private void saveData() {
        Preferences.saveInt(getApplicationContext(), PREF_LOCAL_CHAP_NUM, currentChapNum);
        Preferences.saveInt(getApplicationContext(), PREF_LOCAL_CURRENT_SPEECH, currentSpeech);
    }
}
