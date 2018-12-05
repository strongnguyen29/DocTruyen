package com.strongnguyen.doctruyen;

import org.json.JSONException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testJson() throws JSONException, MalformedURLException {
        String urlPage  = "http://truyenfull.vn/nhat-niem-vinh-hang/chuong-10/";
        URL url = new URL(urlPage);
        String path = url.getPath();
        path = path.replace("//", "/nhat-niem-vinh-hang/");
        String url2 = "http://truyenfull.vn" + path;
        System.out.println(url.getPath());
        System.out.println(url2);
    }
}