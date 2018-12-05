package com.strongnguyen.doctruyen.data;

import com.strongnguyen.doctruyen.model.Book;
import com.strongnguyen.doctruyen.model.Chapter;

import org.junit.Test;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class TruyenFullParserTest {

    @Test
    public void loadBookDocument() {

    }

    @Test
    public void getBookInfo() {
    }

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
    public void getBookId() {
    }

    @Test
    public void getBookTotalPageChapter() {
    }

    @Test
    public void getAjaxHashKey() {
    }

    @Test
    public void quickSearch() {
        String key = URLEncoder.encode("nhat niem vinh hang");
        String rs = TruyenFullParser.getInstance().quickSearch(key);
        System.out.println(rs);
    }

    @Test
    public void searchBook() {
        String key = URLEncoder.encode("nhat niem vinh hang");
        ArrayList<Book> listBook = TruyenFullParser.getInstance().searchBook(key);

        for(Book book : listBook) {
            System.out.println(book.getName() + " | " + book.getUrl());
        }
    }

    @Test
    public void getChapterMapInfo() {
    }

    private void showMsTest(String mes) {
        System.out.println(mes);
    }
}