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
 * Created by Mr Cuong on 11/4/2018.
 * Email: vancuong2941989@gmail.com
 */
public class TruyenCuaTuiParser {
    private static final String TAG = WebTruyenParser.class.getSimpleName();

    private static final String URL_PAGE = "http://truyencuatui.net";

    private static TruyenCuaTuiParser INSTANCE = new TruyenCuaTuiParser();

    private int totalPage = 0;

    /* Ham khoi tao */
    public TruyenCuaTuiParser() {}

    /* Lay doi tuong */
    public static TruyenCuaTuiParser getInstance() {
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
            urlPage = url + "?page=" + String.valueOf(page);
        }

        try {
            Document doc = Jsoup.connect(urlPage).get();
            // Load book id & total page chapter;
            if (doc != null) {

                setTotalPage(doc);

                Elements elementsChap = doc.select("#danh-sach-chuong a.chuong-item");
                for (Element element : elementsChap) {
                    Chapter chapter = new Chapter();
                    chapter.setName(element.text());
                    chapter.setUrl(URL_PAGE + element.attr("href"));
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
            setTotalPage(document);
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
        if (url == null || url.length() < 2) {
            return contentChap;
        }

        try {
            Document doc = Jsoup.connect(url).get();
            if (doc != null) {

                Chapter chapter = new Chapter();
                Element titleElm = doc.select(".breadcrumb li").last();
                chapter.setName(titleElm.text());
                chapter.setUrl(doc.select("a.btn-share").first().attr("data-href"));

                contentChap.setChapter(chapter);

                Element prevElm = doc.select(".previous a").first();
                if (prevElm != null) {
                    contentChap.setPrevUrl(URL_PAGE + prevElm.attr("href"));
                }

                Element nextElm = doc.select(".next a").first();
                if (nextElm != null) {
                    contentChap.setNextUrl(URL_PAGE + nextElm.attr("href"));
                }

                doc.select("script").remove();
                Element elementCont = doc.select(".chapter-content").first();
                for (Element element : elementCont.select("a")) {
                    element.remove();
                }
                elementCont.select("script,a").remove();
                contentChap.setContent(elementCont.html());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentChap;
    }

    private void setTotalPage(Document document) {
        if (totalPage > 0 || document == null) return;

        Elements elements = document.select("#danh-sach-chuong .pagination li");
        Element element;
        if (elements.last().hasClass("disabled")) {
            element = elements.get(elements.size() - 2);
            totalPage = Integer.parseInt(element.text());
            System.out.println("\n Has class");
        } else {
            element = elements.last();
            element = element.select("a").first();
            String url = element.attr("href");
            String[] p = url.split("page=");
            totalPage = Integer.parseInt(p[1]);
        }
    }
}
