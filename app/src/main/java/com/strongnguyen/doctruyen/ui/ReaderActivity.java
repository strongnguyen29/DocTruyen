package com.strongnguyen.doctruyen.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.ReaderService;
import com.strongnguyen.doctruyen.data.TruyenCuaTuiParser;
import com.strongnguyen.doctruyen.data.TruyenCvParser;
import com.strongnguyen.doctruyen.data.TruyenFullParser;
import com.strongnguyen.doctruyen.data.WebTruyenParser;
import com.strongnguyen.doctruyen.model.ContentChap;
import com.strongnguyen.doctruyen.model.ReplaceText;
import com.strongnguyen.doctruyen.ui.popup.PopupReplaceText;
import com.strongnguyen.doctruyen.util.LogUtils;
import com.strongnguyen.doctruyen.util.Preferences;
import com.strongnguyen.doctruyen.util.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.strongnguyen.doctruyen.ui.InputUrlActivity.PREF_URL_BOOK;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.TRUYENCUATUI;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.TRUYENCV;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.TRUYENFULL;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.WEBTRUYEN;
import static com.strongnguyen.doctruyen.ui.popup.PopupReplaceText.PREF_REPLACE_TEXT;

public class ReaderActivity extends AppCompatActivity {
    private static final String TAG = ReaderActivity.class.getSimpleName();
    private static final int REQUEST_LOADER = 2;
    public static final String INTENT_URL = "INTENT_URL_CHAP";
    public static final String INTENT_CURRENT_SPEECH = "INTENT_CURRENT_SPEECH";
    public static final String PREF_CHAP_NAME = "PREF_CHAP_NAME";
    public static final String PREF_CHAP_URL = "PREF_CHAP_URL";
    public static final String PREF_CURRENT_SPEECH = "PREF_CURRENT_SPEECH";

    private PowerManager.WakeLock wakeLock;
    private TelephonyManager mgr;
    private PhoneStateListener phoneStateListener;

    private ReaderService mReaderService;
    private boolean isBound = false;
    private ServiceConnection mServiceConn;

    private TextToSpeech textToSpeech;

    private LoadDataAsync mLoadDataAsync;

    private ScrollView scrollViewContent;
    private TextView tvTitle;
    private TextView tvContent;
    private Button btnReading, btnNext, btnPrev, btnReplace;

    private PopupReplaceText popupReplaceText;

    private ContentChap mContentChap;

    private int currentSpeech;
    private String[] arrTextSpeech;

    private String bookUrl;
    boolean isSpeech = false;

    private int sourceBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        //Keep the CPU on
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "DocTruyen:ReaderWakelock");
        wakeLock.acquire();

        bookUrl = Preferences.getString(getApplicationContext(), PREF_URL_BOOK, "");

        setupTextToSpeech();

        String urlPage = "";
        if (getIntent() != null) {
            urlPage = getIntent().getStringExtra(INTENT_URL);
            try {
                URL urlSource = new URL(urlPage);
                switch (urlSource.getHost()) {
                    case "truyenfull.vn":
                        sourceBook = TRUYENFULL;
                        urlPage = fixURL(urlPage);
                        break;
                    case "truyencv.com":
                        sourceBook = TRUYENCV;
                        break;
                    case "webtruyen.com":
                        sourceBook = WEBTRUYEN;
                        break;
                    case "truyencuatui.net":
                        sourceBook = TRUYENCUATUI;
                        break;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            currentSpeech = getIntent().getIntExtra(INTENT_CURRENT_SPEECH, 0);
        }

        bindView();

        mLoadDataAsync = new LoadDataAsync();
        mLoadDataAsync.execute(urlPage);

        //detechHeadsetButton();
        phoneStateEvent();

        // Khởi tạo ServiceConnection
        mServiceConn = new ServiceConnection() {
            // Phương thức này được hệ thống gọi khi kết nối tới service bị lỗi
            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }

            // Phương thức này được hệ thống gọi khi kết nối tới service thành công
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                ReaderService.ReaderBinder binder = (ReaderService.ReaderBinder) service;
                mReaderService = binder.getService(); // lấy đối tượng MyService
                isBound = true;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        saveData();
        if (popupReplaceText != null) {
            popupReplaceText.destroy();
        }
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mLoadDataAsync != null) {
            mLoadDataAsync.cancel(true);
            mLoadDataAsync = null;
        }
        textToSpeech.stop();
        textToSpeech.shutdown();

        wakeLock.release();
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
                }
            }
        });
        btnReplace = findViewById(R.id.btn_replace );
        btnReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPopupReplace();
                popupReplaceText.show();
            }
        });
    }

    private void setupPopupReplace() {
        if (popupReplaceText == null) {
            popupReplaceText = new PopupReplaceText(ReaderActivity.this, new PopupReplaceText.OnPopupListener() {
                @Override
                public void onReplaceTexts(ReplaceText replaceText) {
                    if (isSpeech) {
                        textToSpeech.stop();
                    }
                    // Tach text thanh mang;
                    String text = tvContent.getText().toString();
                    text = text.replace(replaceText.getCurrentText(), replaceText.getNewText());
                    tvContent.setText(text);
                    arrTextSpeech = text.split("\n");
                    if (isSpeech) {
                        speakOut();
                    }
                    LogUtils.d(TAG, "onReplaceTexts");
                }
            });
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
     * Next chapter
     */
    private void nextChapter() {
        LogUtils.d(TAG, "nextChapter()");
        mLoadDataAsync = new LoadDataAsync();
        mLoadDataAsync.execute(mContentChap.getNextUrl());
        currentSpeech = 0;
        scrollViewContent.scrollTo(0,0);
    }

    /**
     * Prev next;
     */
    private void prevChapter() {
        LogUtils.d(TAG, "prevChapter()");
        mLoadDataAsync = new LoadDataAsync();
        mLoadDataAsync.execute(mContentChap.getPrevUrl());
        currentSpeech = 0;
        scrollViewContent.scrollTo(0,0);
    }

    /**
     * Save data current reading;
     */
    private void saveData() {
        if (mContentChap == null) return;
        Preferences.saveInt(getApplicationContext(), PREF_CURRENT_SPEECH, currentSpeech);
        LogUtils.d(TAG, "saveData: PREF_CURRENT_SPEECH = " + currentSpeech);

        if (mContentChap.getChapter() == null) return;
        Preferences.saveString(getApplicationContext(), PREF_CHAP_NAME, mContentChap.getChapter().getName());
        Preferences.saveString(getApplicationContext(), PREF_CHAP_URL, mContentChap.getChapter().getUrl());

        LogUtils.d(TAG, "saveData: PREF_CHAP_NAME = " + mContentChap.getChapter().getName());
        LogUtils.d(TAG, "saveData: PREF_CHAP_URL = " + mContentChap.getChapter().getUrl());
        //EventBus.getDefault().post(new SaveDataEventBus(mContentChap.getChapter().getName()));
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveData();
        LogUtils.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    /**
     * Fix url error;
     * @param url1
     * @return
     */
    private String fixURL(String url1) {
        String url2 = url1;
        try {
            URL urlBook = new URL(bookUrl);
            String pathBook = urlBook.getPath();
            pathBook = pathBook.replace("/","");
            URL url = new URL(url1);
            String path = url.getPath();
            path = path.replace("//", "/" + pathBook + "/");
            url2 = "http://truyenfull.vn" + path;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url2;
    }

    private String replaceText(String text) {
        if (text == null || text.length() < 3) return text;

        String s = text;
        String json = Preferences.getString(getApplicationContext(), PREF_REPLACE_TEXT, "");
        if (json.length() > 2) {
            ArrayList<ReplaceText> list = new Gson()
                    .fromJson(json, new TypeToken<List<ReplaceText>>(){}.getType());

            for (ReplaceText replaceText : list) {
                s = s.replace(replaceText.getCurrentText(), replaceText.getNewText());
                LogUtils.d(TAG, "REPLACE: " + replaceText.getCurrentText() + " TO : " + replaceText.getNewText());
            }
        }
        return s;
    }

    /**
     * Class asynctask load data chapter;
     */
    public class LoadDataAsync extends AsyncTask<String, Void, ContentChap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ContentChap doInBackground(String... url) {
            switch (sourceBook) {
                case TRUYENFULL:
                    return TruyenFullParser.getInstance().getContentChap(url[0]);
                case TRUYENCV:
                    return TruyenCvParser.getInstance().getContentChap(url[0]);
                case WEBTRUYEN:
                    return WebTruyenParser.getInstance().getContentChap(url[0]);
                case TRUYENCUATUI:
                    return TruyenCuaTuiParser.getInstance().getContentChap(url[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ContentChap data) {
            super.onPostExecute(data);
            LogUtils.d(TAG, "onPostExecute()");
            if (data != null) {
                mContentChap = data;
                if (data.getChapter() != null) {
                    tvTitle.setText(data.getChapter().getName());
                }
                tvContent.setText(TextUtils.styleTextHtml(mContentChap.getContent()));
                btnPrev.setEnabled(data.getPrevUrl() != null && data.getPrevUrl().length() > 2);
                btnNext.setEnabled(data.getNextUrl() != null && data.getNextUrl().length() > 2);

                // Tach text thanh mang;
                String text = tvContent.getText().toString();
                text = text.replace("'", "");
                text = replaceText(text);
                tvContent.setText(text);
                arrTextSpeech = text.split("\n");

                if (isSpeech) {
                    speakOut();
                }

                saveData();
            }
        }
    }
}
