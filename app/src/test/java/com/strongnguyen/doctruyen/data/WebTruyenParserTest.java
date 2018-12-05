package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 9/17/2018.
 * Email: vancuong2941989@gmail.com
 */
public class WebTruyenParserTest {

    @Test
    public void getListChapter() {
        String url = "http://webtruyen.com/tu-chan-noi-chuyen-phiem-quan/";
        ArrayList<Chapter> list = WebTruyenParser.getInstance().getListChapter(url, 2);

        for (Chapter chapter : list) {
            showMsTest(chapter.getName() + " >>>> " + chapter.getUrl());
        }
    }

    @Test
    public void getBookTotalPageChapter() {
        String url = "http://webtruyen.com/tu-chan-noi-chuyen-phiem-quan/";
        int page = WebTruyenParser.getInstance().getBookTotalPageChapter(url);
        showMsTest(String.valueOf(page));
    }

    @Test
    public void getContentChap() {
        String url = "http://webtruyen.com/tu-chan-noi-chuyen-phiem-quan/bac-ha-tan-nhan-dau-thau-ruot-gan_1366688.html";
        ContentChap contentChap = WebTruyenParser.getInstance().getContentChap(url);
        showMsTest("Name chap: " + contentChap.getChapter().getName());
        showMsTest("Prev chap: " + contentChap.getPrevUrl());
        showMsTest("Next chap: " + contentChap.getNextUrl());
        showMsTest("Content chap: \n" + contentChap.getContent());
    }

    private void showMsTest(String mes) {
        System.out.println(mes);
    }
}