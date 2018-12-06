package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The Class
 * Created by pc on 12/6/2018.
 */
public class WikiDichParserTest {

    String url = "https://wikidich.com/truyen/de-ba-WLKJVnCVfEu8FYzf";

    @Test
    public void getListChapter() {
        ArrayList<Chapter> list = WikiDichParser.getInstance().getListChapter(url, 82);
        for (Chapter chapter : list) {
            showMsg("Name = " + chapter.getName() + " | link = " + chapter.getUrl());
        }
    }

    @Test
    public void getBookTotalPageChapter() throws IOException {
        String url1 = "https://wikidich.com/truyen/de-ba-WLKJVnCVfEu8FYzf";
        String url2 = "https://wikidich.com/truyen/dai-duong-chi-thien-co-de-vuong-W4zahlS4CA1~i9Qy";

        Document doc1 = Jsoup.connect(url).get();
        int totalPage = WikiDichParser.getInstance().getTotalPage(doc1);
        showMsg("total page = 31 | " + String.valueOf(totalPage));

        Document doc2 = Jsoup.connect(url1).get();
        totalPage = WikiDichParser.getInstance().getTotalPage(doc2);
        showMsg("total page = 81 | " + String.valueOf(totalPage));

        Document doc3 = Jsoup.connect(url2).get();
        totalPage = WikiDichParser.getInstance().getTotalPage(doc3);
        showMsg("total page = 6 | " + String.valueOf(totalPage));
    }

    @Test
    public void getContentChap() {
        String url = "https://wikidich.com/truyen/de-ba/chuong-3431-1-kiem-chem-giet-W96KNMQsREhgl05V";
        ContentChap contentChap = WikiDichParser.getInstance().getContentChap(url);
        showMsg("Name chap: " + contentChap.getChapter().getName());
        showMsg("Prev chap: " + contentChap.getPrevUrl());
        showMsg("Next chap: " + contentChap.getNextUrl());
        showMsg("Content chap: \n" + contentChap.getContent());
    }

    @Test
    public void getBookId() throws IOException {
        Document doc = Jsoup.connect(url).get();
        String bookId = WikiDichParser.getInstance().getBookId(doc);
        showMsg(bookId);
    }

    private void showMsg(String s) {
        System.out.println("\n" + s);
    }
}