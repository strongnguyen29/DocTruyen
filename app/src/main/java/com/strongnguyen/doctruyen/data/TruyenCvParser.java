package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;
import com.strongnguyen.doctruyen.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 9/12/2018.
 * Email: vancuong2941989@gmail.com
 */
public class TruyenCvParser {

    private static final String TAG = TruyenCvParser.class.getSimpleName();

    private static final String URL_PAGE = "https://truyencv.com/";

    private static TruyenCvParser INSTANCE = new TruyenCvParser();

    private ArrayList<Chapter> listChapter = new ArrayList<>();

    private int bookId = 0;

    private int totalPage = 0;

    /* Ham khoi tao */
    public TruyenCvParser() {}

    /* Lay doi tuong */
    public static TruyenCvParser getInstance() {
        return(INSTANCE);
    }

    public void getBookId(String url) {
        if (bookId > 0) return;
    }

    public int getTotalPage() {
        if (totalPage > 0) return totalPage;

        if (listChapter.size() <= 50) {
            totalPage = 1;
        } else {
            int du = listChapter.size() % 50;
            totalPage = listChapter.size() / 50;
            if (du > 0) {
                totalPage += 1;
            }

        }
        return totalPage;
    }

    /**
     * Tai danh sach chuong;
     *
     * @param url url book
     * @param page trang;
     * @return arraylist chuong;
     */
    public ArrayList<Chapter> getListChapter(String url, int page) {
        ArrayList<Chapter> listChap = new ArrayList<>();

        if (listChapter.size() > 0) {
            // Da co san du lieu
            if (page * 50 > listChapter.size()) {
                listChap.addAll(listChapter.subList((page - 1) * 50, listChap.size() - 1));
            } else {
                listChap.addAll(listChapter.subList((page - 1) * 50, page * 50 - 1));
            }
        } else {
            // chua co san du lieu => load ve;
            listChapter = loadListChapter(url);
            if (listChapter.size() > 0) {
                if (page * 50 > listChapter.size()) {
                    listChap.addAll(listChapter.subList((page - 1) * 50, listChapter.size() - 1));
                } else {
                    listChap.addAll(listChapter.subList((page - 1) * 50, page * 50 - 1));
                }
            }
        }
        return listChap;
    }

    /**
     * Tai noi dung chuong;
     *
     * @param url url book
     * @return ContentChap
     */
    public ContentChap getContentChap(String url) {
        ContentChap contentChap = new ContentChap();
        if (url == null || url.length() == 0) {
            return contentChap;
        }

        try {
            Document doc = Jsoup.connect(url).get();
            if (doc != null) {
                Chapter chapter = new Chapter();
                Element titleElm = doc.select("#js-truyencv-read-content .title").first();
                chapter.setName(titleElm.text());
                chapter.setUrl(url);

                contentChap.setChapter(chapter);
                Element prevNext = doc.select(".truyencv-read-navigation").first();

                Element prevElm = doc.select(".truyencv-read-navigation > a").first();
                if (prevElm != null) {
                    contentChap.setPrevUrl(prevElm.attr("href"));
                }

                Element nextElm = doc.select(".truyencv-read-navigation > a").last();
                if (nextElm != null) {
                    contentChap.setNextUrl(nextElm.attr("href"));
                }
                doc.select("script").remove();
                Element elementCnt = doc.select("#js-truyencv-content").first();
                elementCnt.select("a").parents().remove();
                contentChap.setContent(elementCnt.html());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentChap;
    }

    /**
     * Get info load list chapter;
     * @param str url page
     * @return Array info;
     */
    private String[] getPostInfo(String str) {
        if (str == null || str.length() == 0) return null;

        str = str.replace("showChapter", "");
        str = str.replace("(", "");
        str = str.replace(")", "");
        str = str.replace("'", "");
        return str.split(",");
    }

    public ArrayList<Chapter> loadListChapter(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Element elmBookId = document.getElementsByAttributeValue("aria-controls", "truyencv-detail-chap").first();
            String[] postInfo = getPostInfo(elmBookId.attr("onClick"));
            if (postInfo != null) {
                OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("showChapter", "1")
                        .add("media_id", postInfo[0])
                        .add("number", postInfo[1])
                        .add("page", postInfo[2])
                        .add("type", postInfo[3])
                        .build();
                Request request = new Request.Builder()
                        .url(URL_PAGE + "index.php")
                        .post(formBody)
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                Document doc = Jsoup.parseBodyFragment(data);

                ArrayList<Chapter> listChap = new ArrayList<>();

                for (Element element : doc.select(".item a")) {
                    Chapter chapter = new Chapter();
                    chapter.setName(element.text());
                    chapter.setUrl(element.attr("href"));
                    listChap.add(chapter);
                }
                Collections.reverse(listChap);
                return listChap;
            }
        } catch (IOException e) {
            LogUtils.d(TAG, "Document is Null, Msg: " + e.getMessage());
        }

        return new ArrayList<>();
    }
}
