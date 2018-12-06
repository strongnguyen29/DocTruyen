package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;
import com.strongnguyen.doctruyen.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The Class
 * Created by pc on 12/6/2018.
 */
public class WikiDichParser {

    private static final String TAG = WikiDichParser.class.getSimpleName();

    private static final String URL_PAGE = "https://wikidich.com";

    private static WikiDichParser INSTANCE = new WikiDichParser();

    private String bookId;

    private int totalPage = 0;

    public WikiDichParser() {}

    /**
     * Lay doi tuong
     */
    public static WikiDichParser getInstance() {
        return(INSTANCE);
    }

    public ArrayList<Chapter> getListChapter(String url, int page) {
        ArrayList<Chapter> listChap = new ArrayList<>();
        if (page == 0) page = 1;
        if (totalPage > 0 && page > totalPage) return listChap;

        try {
            Document doc = Jsoup.connect(url).get();
            // Load book id;
            getBookId(doc);
            // Load total book;
            getTotalPage(doc);
            // Build url load;

            String urlAjax = URL_PAGE + "/book/index?";
            urlAjax += "&bookId=" + bookId;
            urlAjax += "&start=" + (page - 1)*50;
            urlAjax += "&size=50";
            LogUtils.d(TAG, "getListChapter: urlAjax = " + urlAjax);
            System.out.println("getListChapter: urlAjax = " + urlAjax);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlAjax)
                    .build();

            Response response = client.newCall(request).execute();
            String data = response.body().string();
            Document doc2 = Jsoup.parseBodyFragment(data);

            for (Element element : doc2.select(".chapter-name a")) {
                Chapter chapter = new Chapter();
                chapter.setName(element.text());
                chapter.setUrl(URL_PAGE + element.attr("href"));
                listChap.add(chapter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listChap;
    }

    /**
     * Get total page chapter
     * @param url link book;
     * @return total page;
     */
    public int getBookTotalPageChapter(String url) {
        if (totalPage > 0) return totalPage;
        try {
            Document doc = Jsoup.connect(url).get();
            getTotalPage(doc);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalPage;
    }

    public int getTotalPage(Document doc) {
        if (totalPage > 0) return totalPage;

        if (doc != null) {
            Element element = doc.select(".volume-list .pagination li a").last();
            int totalChap;
            if (element == null) {
                totalPage = 1;
                Elements elements = doc.select(".chapter-name");
                totalChap = elements.size();
            } else {
                int start = Integer.parseInt(element.attr("data-start"));
                int size = Integer.parseInt(element.attr("data-size"));
                totalChap = start + size;
            }

            totalPage = totalChap/50;
            if (totalChap%50 > 0) totalPage += 1;
        }

        return totalPage;
    }

    /**
     * Get content chapter;
     *
     * @param url
     * @return
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
                Element titleElm = doc.select(".book-title").get(1);
                chapter.setName(titleElm.text());
                chapter.setUrl(url);

                contentChap.setChapter(chapter);
                Element prevElm = doc.select("#btnPreChapter").first();
                if (prevElm != null) {
                    contentChap.setPrevUrl(URL_PAGE + prevElm.attr("href"));
                }
                Element nextElm = doc.select("#btnNextChapter").first();
                if (nextElm != null) {
                    contentChap.setNextUrl(URL_PAGE + nextElm.attr("href"));
                }

                contentChap.setContent(doc.select("#bookContentBody").first().html());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentChap;
    }

    public String getBookId(Document doc) {
        if (doc != null) {
            Element element = doc.select("#bookId").first();
            bookId = element.attr("value");
        }
        return bookId;
    }
}
