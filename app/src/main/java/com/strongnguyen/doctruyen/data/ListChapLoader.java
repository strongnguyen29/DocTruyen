package com.strongnguyen.doctruyen.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.strongnguyen.doctruyen.ui.ListChapActivity.TRUYENCUATUI;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.TRUYENCV;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.TRUYENFULL;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.WEBTRUYEN;
import static com.strongnguyen.doctruyen.ui.ListChapActivity.WIKIDICH;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ListChapLoader extends AsyncTaskLoader<List<Chapter>> {
    private static final String TAG = ListChapLoader.class.getSimpleName();

    private final String url;
    private boolean isLoaded = false;
    private int page = 1;
    private int sourceBook;

    public ListChapLoader(Context context, String url, int sourceBook) {
        super(context);
        this.url = url;
        this.sourceBook = sourceBook;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    protected void onStartLoading() {
        LogUtils.d(TAG, "onStartLoading : isLoaded = " + isLoaded);
        if (!isLoaded) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        LogUtils.d(TAG, "onStopLoading");
        cancelLoad();
    }

    @Override
    public List<Chapter> loadInBackground() {
        LogUtils.d(TAG, "loadInBackground");
        switch (sourceBook) {
            case TRUYENFULL:
                return TruyenFullParser.getInstance().getListChapter(url, page);
            case TRUYENCV:
                return TruyenCvParser.getInstance().getListChapter(url, page);
            case WEBTRUYEN:
                return WebTruyenParser.getInstance().getListChapter(url, page);
            case TRUYENCUATUI:
                return TruyenCuaTuiParser.getInstance().getListChapter(url, page);
            case WIKIDICH:
                return WikiDichParser.getInstance().getListChapter(url, page);
        }
        return new ArrayList<>();
    }
}
