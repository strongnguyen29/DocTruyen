package com.strongnguyen.doctruyen.ui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.strongnguyen.doctruyen.R;
import com.strongnguyen.doctruyen.data.ListChapLoader;
import com.strongnguyen.doctruyen.data.TruyenCuaTuiParser;
import com.strongnguyen.doctruyen.data.TruyenCvParser;
import com.strongnguyen.doctruyen.data.TruyenFullParser;
import com.strongnguyen.doctruyen.data.WebTruyenParser;
import com.strongnguyen.doctruyen.eventbus.SaveDataEventBus;
import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.util.LogUtils;
import com.strongnguyen.doctruyen.util.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.strongnguyen.doctruyen.ui.InputUrlActivity.PREF_URL_BOOK;

public class ListChapActivity extends AppCompatActivity {
    private static final String TAG = ListChapActivity.class.getSimpleName();
    private static final int REQUEST_LOADER = 1;
    private static final String PREF_CURRENT_PAGE = "pref_current_page";
    public static final int TRUYENFULL = 1;
    public static final int TRUYENCV = 2;
    public static final int WEBTRUYEN = 3;
    public static final int TRUYENCUATUI = 4;

    private LoaderManager mLoaderManager;
    private ListChapLoader mListChapLoader;

    private RecyclerView rvListChap, rvListChapPage;
    private TextView btnDocTiep;

    private ListChapAdapter listChapAdapter;
    private ListChapPageAdapter listPageAdapter;

    private ArrayList<Chapter> listChaps = new ArrayList<>();
    private ArrayList<Integer> listChapPage = new ArrayList<>();
    private int curentPage;

    private String bookUrl;

    private int sourceBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chap);
        EventBus.getDefault().register(this);
        curentPage = Preferences.getInt(getApplicationContext(), PREF_CURRENT_PAGE, 1);
        bookUrl = Preferences.getString(getApplicationContext(), PREF_URL_BOOK, "");

        try {
            URL urlSource = new URL(bookUrl);
            if (urlSource.getHost().equals("truyenfull.vn")) {
                sourceBook = TRUYENFULL;
            } else if (urlSource.getHost().equals("truyencv.com")) {
                sourceBook = TRUYENCV;
            } else if (urlSource.getHost().equals("webtruyen.com")) {
                sourceBook = WEBTRUYEN;
            } else if (urlSource.getHost().equals("truyencuatui.net")) {
                sourceBook = TRUYENCUATUI;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        bindView();

        mLoaderManager = getSupportLoaderManager();
        mListChapLoader = new ListChapLoader(this, bookUrl, sourceBook);
        mListChapLoader.setPage(curentPage);
        mLoaderManager.initLoader(REQUEST_LOADER, null, mLoaderCallbacks);

        if (sourceBook == TRUYENFULL || sourceBook == WEBTRUYEN || sourceBook == TRUYENCUATUI) {
            // Get totalpage;
            new AsynTotalPage().execute();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void saveDataEventBus(SaveDataEventBus eventBus) {
        if (eventBus.getChapName() != null) {
            btnDocTiep.setText(eventBus.getChapName());
            btnDocTiep.setEnabled(true);
        }
    }

    /**
     * Bind view;
     */
    public void bindView() {
        btnDocTiep = findViewById(R.id.tv_btn_doc_tiep);
        btnDocTiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Preferences.getString(
                        getApplicationContext(), ReaderActivity.PREF_CHAP_URL, null);
                int currentSpeech = Preferences.getInt(
                        getApplicationContext(), ReaderActivity.PREF_CURRENT_SPEECH, 0);

                Intent intent = new Intent(ListChapActivity.this, ReaderActivity.class);
                intent.putExtra(ReaderActivity.INTENT_URL, url);
                intent.putExtra(ReaderActivity.INTENT_CURRENT_SPEECH, currentSpeech);
                startActivity(intent);
            }
        });

        String chapName = Preferences.getString(
                getApplicationContext(), ReaderActivity.PREF_CHAP_NAME, null);
        if (chapName != null) {
            btnDocTiep.setText(chapName);
            btnDocTiep.setEnabled(true);
        } else {
            btnDocTiep.setText("Chưa lưu");
            btnDocTiep.setEnabled(false);
        }

        rvListChap = findViewById(R.id.rv_listchap);
        rvListChap.setHasFixedSize(true);
        rvListChap.setLayoutManager(new LinearLayoutManager(this));
        listChapAdapter = new ListChapAdapter(listChaps, new ListChapAdapter.OnListener() {
            @Override
            public void onClickListener(String url) {
                Intent intent = new Intent(ListChapActivity.this, ReaderActivity.class);
                intent.putExtra(ReaderActivity.INTENT_URL, url);
                startActivity(intent);
            }
        });
        rvListChap.setAdapter(listChapAdapter);

        rvListChapPage = findViewById(R.id.rv_listchap_page);
        rvListChapPage.setHasFixedSize(true);
        rvListChapPage.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        listPageAdapter = new ListChapPageAdapter(listChapPage, curentPage, new ListChapPageAdapter.OnListener() {
            @Override
            public void onClickListener(int number) {
                curentPage = number;
                Preferences.saveInt(getApplicationContext(), PREF_CURRENT_PAGE, curentPage);
                listPageAdapter.setPageAcitve(curentPage);
                mListChapLoader.setPage(curentPage);
                mListChapLoader.setLoaded(false);
                mListChapLoader.startLoading();
            }
        });
        rvListChapPage.setAdapter(listPageAdapter);
    }

    private LoaderManager.LoaderCallbacks<List<Chapter>> mLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<List<Chapter>>() {
        @Override
        public Loader<List<Chapter>> onCreateLoader(int i, Bundle bundle) {
            LogUtils.d(TAG, "onCreateLoader");
            return mListChapLoader;
        }

        @Override
        public void onLoadFinished(Loader<List<Chapter>> loader, List<Chapter> chapters) {
            LogUtils.d(TAG, "onLoadFinished: " + chapters.size());
            if (chapters.size() > 0) {
                listChaps.clear();
                listChaps.addAll(chapters);
                listChapAdapter.notifyDataSetChanged();
            }

            if (sourceBook == TRUYENCV) {
                int totalPage = TruyenCvParser.getInstance().getTotalPage();
                if (totalPage > 0) {
                    listChapPage.clear();
                    for (int i = 1; i <= totalPage; i++) {
                        listChapPage.add(i);
                    }

                    listPageAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Chapter>> loader) {

        }
    };

    public class AsynTotalPage extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            switch (sourceBook) {
                case TRUYENFULL:
                    return TruyenFullParser.getInstance().getBookTotalPageChapter(bookUrl);
                case WEBTRUYEN:
                    return WebTruyenParser.getInstance().getBookTotalPageChapter(bookUrl);
                case TRUYENCUATUI:
                    return TruyenCuaTuiParser.getInstance().getBookTotalPageChapter(bookUrl);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            LogUtils.d(TAG, "onPostExecute: " + integer);
            if (integer > 0) {
                listChapPage.clear();
                for (int i = 1; i <= integer; i++) {
                    listChapPage.add(i);
                }
                listPageAdapter.notifyDataSetChanged();
            }
        }
    }

}
