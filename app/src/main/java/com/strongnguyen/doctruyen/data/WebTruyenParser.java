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

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 9/17/2018.
 * Email: vancuong2941989@gmail.com
 */
public class WebTruyenParser {
    private static final String TAG = WebTruyenParser.class.getSimpleName();

    private static final String URL_PAGE = "http://webtruyen.com";

    private static WebTruyenParser INSTANCE = new WebTruyenParser();

    private int totalPage = 0;

    /* Ham khoi tao */
    public WebTruyenParser() {}

    /* Lay doi tuong */
    public static WebTruyenParser getInstance() {
        return(INSTANCE);
    }

    /**
     * Load book info and list chapter;
     *
     * @param url link book;
     */

    public ArrayList<Chapter> getListChapter(String url, int page) {
        ArrayList<Chapter> listChap = new ArrayList<>();
        String urlPage = url;
        if (page > 1) {
            urlPage = url + String.valueOf(page);
        }

        try {
            Document doc = Jsoup.connect(urlPage).get();
            // Load book id & total page chapter;
            if (doc != null) {
                Elements elementsChap = doc.select("#divtab ul li h4 a");
                for (Element element : elementsChap) {
                    Chapter chapter = new Chapter();
                    chapter.setName(element.text());
                    chapter.setUrl(element.attr("href"));
                    listChap.add(chapter);
                }
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
            Document document = Jsoup.connect(url).get();
            if (document != null) {
                Element elementPage = document.select(".numbpage").first();
                String[] pageStr = elementPage.text().replace(" ", "").split("/");
                totalPage = Integer.parseInt(pageStr[1]);
            }
        } catch (IOException e) {
            LogUtils.d(TAG, "Document is Null, Msg: " + e.getMessage());
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
                Element titleElm = doc.select("#reading .chapter-header ul li h3").first();
                chapter.setName(titleElm.text());
                chapter.setUrl(url);

                contentChap.setChapter(chapter);

                Element prevElm = doc.select("#prevchap").first();
                if (prevElm != null) {
                    contentChap.setPrevUrl(prevElm.attr("href"));
                }
                Element nextElm = doc.select("#nextchap").first();
                if (nextElm != null) {
                    contentChap.setNextUrl(nextElm.attr("href"));
                }

                contentChap.setContent(doc.select("#content").first().html());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentChap;
    }
}
