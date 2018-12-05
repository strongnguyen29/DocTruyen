package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 11/4/2018.
 * Email: vancuong2941989@gmail.com
 */
public class TruyenCuaTuiParserTest {

    @Test
    public void getListChapter() {
        String url = "http://truyencuatui.net/truyen/trinh-quan-nhan-nhan.html";
        ArrayList<Chapter> list = TruyenCuaTuiParser.getInstance().getListChapter(url, 2);

        for (Chapter chapter : list) {
            showMsTest(chapter.getName() + " >>>> " + chapter.getUrl());
        }
    }

    @Test
    public void getBookTotalPageChapter() {

    }

    @Test
    public void getContentChap() {
        String url = "http://truyencuatui.net/truyen/trinh-quan-nhan-nhan/chuong-667-dai-an-khong-hoi-bao/1650133.html";
        ContentChap contentChap = TruyenCuaTuiParser.getInstance().getContentChap(url);
        showMsTest("Name chap: " + contentChap.getChapter().getName());
        showMsTest("URL chap: " + contentChap.getChapter().getUrl());
        showMsTest("Prev chap: " + contentChap.getPrevUrl());
        showMsTest("Next chap: " + contentChap.getNextUrl());
        showMsTest("Content chap: \n" + contentChap.getContent());
    }

    @Test
    public void setTotalPage() throws IOException {

    }

    private void showMsTest(String mes) {
        System.out.println("\n" + mes);
    }
}