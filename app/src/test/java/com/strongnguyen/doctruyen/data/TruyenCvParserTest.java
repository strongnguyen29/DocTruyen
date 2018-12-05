package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 9/15/2018.
 * Email: vancuong2941989@gmail.com
 */
public class TruyenCvParserTest {

    @Test
    public void getBookId() {
        String str = "showChapter(4533,1,1,'tu chan noi chuyen phiem quan')";
        str = str.replace("showChapter", "");
        showMsTest(str);
        str = str.replace("(", "");
        showMsTest(str);
        str = str.replace(")", "");
        showMsTest(str);
        str = str.replace("'", "");
        showMsTest(str);
        String[] atrArr = str.split(",");

        for (String i : atrArr) {
            showMsTest(i);
        }
    }

    @Test
    public void loadListChapter() {
        String url = "https://truyencv.com/tu-chan-noi-chuyen-phiem-quan/";
        ArrayList<Chapter> data = TruyenCvParser.getInstance().loadListChapter(url);
        for (Chapter chapter : data) {
            showMsTest("Name: " + chapter.getName());
            showMsTest("URL: " + chapter.getUrl());
        }
    }

    @Test
    public void getContentChap() {
        String url = "https://truyencv.com/tu-chan-noi-chuyen-phiem-quan/chuong-2178/";
        ContentChap contentChap = TruyenCvParser.getInstance().getContentChap(url);
        showMsTest("Name chap: " + contentChap.getChapter().getName());
        showMsTest("Prev chap: " + contentChap.getPrevUrl());
        showMsTest("Next chap: " + contentChap.getNextUrl());
        showMsTest("Content chap: \n" + contentChap.getContent());
    }

    private void showMsTest(String mes) {
        System.out.println(mes);
    }
}