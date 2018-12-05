package com.strongnguyen.doctruyen.data;

import android.support.test.runner.AndroidJUnit4;

import com.strongnguyen.doctruyen.model.Chapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
@RunWith(AndroidJUnit4.class)
public class TruyenFullParserTest {

    @Test
    public void getListChapter() {
        showMsTest("Test lay du lieu");
        String url = "http://truyenfull.vn/nhat-niem-vinh-hang";
        ArrayList<Chapter> chapters = TruyenFullParser.getInstance().getListChapter(url, 1);

        showMsTest("size = " + chapters.size());

        for (Chapter chapter : chapters) {
            showMsTest(chapter.getName());
        }
    }

    @Test
    public void getContentChap() {
    }

    @Test
    public void quickSearch() {
    }

    @Test
    public void searchBook() {
    }

    private void showMsTest(String mes) {
        System.out.println(mes);
    }
}