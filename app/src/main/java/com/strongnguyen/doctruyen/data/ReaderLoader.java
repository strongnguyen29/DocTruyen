package com.strongnguyen.doctruyen.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.strongnguyen.doctruyen.model.ContentChap;
import com.strongnguyen.doctruyen.util.LogUtils;

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
public class ReaderLoader extends AsyncTaskLoader<ContentChap> {
    private static final String TAG = ReaderLoader.class.getSimpleName();

    private String url;
    private boolean isLoaded = false;
    private int sourceBook;

    public ReaderLoader(Context context, int sourceBook) {
        super(context);
        this.sourceBook = sourceBook;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void setUrl(String url) {
        this.url = url;
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
    public ContentChap loadInBackground() {
        switch (sourceBook) {
            case TRUYENFULL:
                return TruyenFullParser.getInstance().getContentChap(url);
            case TRUYENCV:
                return TruyenCvParser.getInstance().getContentChap(url);
            case WEBTRUYEN:
                return WebTruyenParser.getInstance().getContentChap(url);
            case TRUYENCUATUI:
                return TruyenCuaTuiParser.getInstance().getContentChap(url);
            case WIKIDICH:
                return WikiDichParser.getInstance().getContentChap(url);
        }
        return null;
    }
}
