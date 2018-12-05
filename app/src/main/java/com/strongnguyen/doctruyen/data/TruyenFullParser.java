package com.strongnguyen.doctruyen.data;

import com.google.gson.Gson;
import com.strongnguyen.doctruyen.model.Book;
import com.strongnguyen.doctruyen.model.Chapter;
import com.strongnguyen.doctruyen.model.ContentChap;
import com.strongnguyen.doctruyen.model.PaserListChap;
import com.strongnguyen.doctruyen.util.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class TruyenFullParser {
    private static final String TAG = TruyenFullParser.class.getSimpleName();

    private static final String URL_PAGE = "http://truyenfull.vn/";

    private static final long EXPIRES_HASH = 30 * 60 * 1000; // Milisecond <=> 30 phut

    private static TruyenFullParser INSTANCE = new TruyenFullParser();

    private static String hashKey;

    private static long timeGetHash; // Milisecond

    private Document document;

    /* Ham khoi tao */
    public TruyenFullParser() {}

    /* Lay doi tuong */
    public static TruyenFullParser getInstance() {
        return(INSTANCE);
    }

    /**
     * Load book info and list chapter;
     *
     * @param url link book;
     */
    public void loadBookDocument(String url) {
        if (document == null || document.select("body#body_truyen").first() == null) {
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                document = null;
                LogUtils.d(TAG, "Document is Null, Msg: " + e.getMessage());
            }
        }
    }

    /**
     * Get book info;
     *
     * @param url link page book info;
     * @return The {@link Book}
     */
    public Book getBookInfo(String url) {
        loadBookDocument(url);

        if (document != null) {
            Book book = new Book();

            book.setId(document.getElementById("truyen-id").attr("value"));

            Element infoDescElement = document.select("div.col-info-desc").first();

            Element imgElement = infoDescElement.select("img").first();
            if (imgElement != null) {
                book.setPoster(imgElement.attr("src"));
            }

            Element titleElement = infoDescElement.select("h3.title").first();
            if (titleElement != null) {
                book.setName(titleElement.text());
            }

            Element infoElement = infoDescElement.select("div.info").first();

            for (Element element : infoElement.select("div")) {
                if (element.select("a").first() != null) {
                    // Author or category
                    if (element.select("a").first().attr("itemprop").equalsIgnoreCase("author")) {
                        book.setAuthor(element.select("a").first().text());
                    } else {
                        book.setCategory(element.text().replace("Thể loại:", ""));
                    }
                } else if (element.select(".source").first() != null){
                    book.setSource(element.select(".source").first().text());
                } else if (element.select(".text-primary").first() != null){
                    book.setFull(false);
                } else if (element.select(".text-success").first() != null){
                    book.setFull(true);
                }
            }

            // rating
            for (Element element : infoDescElement.select(".rate span")) {
                if (element.attr("itemprop").equalsIgnoreCase("ratingValue")) {
                    book.setRating(Double.parseDouble(element.text()));
                } else if (element.attr("itemprop").equalsIgnoreCase("ratingCount")) {
                    book.setRatingCount(Long.parseLong(element.text()));
                }
            }

            Element descElement = infoDescElement.select(".desc-text").first();
            if (descElement != null) {
                book.setDescription(descElement.html());
            }

            Element firstchapElement = document.select("#list-chapter .list-chapter li a").first();
            if (firstchapElement != null) {
                book.setFirstChapterUrl(firstchapElement.attr("href"));
            }

            return book;
        }

        return null;
    }

    /**
     * Get list chapter of book without page;
     *
     * @param url link book;
     * @param page page number;
     * @return list {@link Chapter}
     */
    public ArrayList<Chapter> getListChapter(String url, int page) {
        ArrayList<Chapter> listChap = new ArrayList<>();

        loadBookDocument(url);
        // Load book id & total page chapter;
        int bookId = 0;
        int totalPage = 0;
        if (document != null) {
            bookId = Integer.parseInt(document.getElementById("truyen-id").attr("value"));
            totalPage = Integer.parseInt(document.getElementById("total-page").attr("value"));
        }

        if (bookId == 0 || totalPage == 0 || page > totalPage) {
            return listChap;
        }

        // Build url load;
        String urlAjax = URL_PAGE + "ajax.php?type=list_chapter";
        urlAjax += "&tid=" + bookId;
        urlAjax += "&page=" + page + "&totalp=" + totalPage;
        urlAjax += "&hash=" + getAjaxHashKey();
        LogUtils.d(TAG, "getListChapter: urlAjax = " + urlAjax);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlAjax)
                    .build();

            Response response = client.newCall(request).execute();

            String json = response.body().string();
            PaserListChap paserListChap = new Gson().fromJson(json, PaserListChap.class);
            if (paserListChap != null) {
                Document doc = Jsoup.parseBodyFragment(paserListChap.chap_list);
                for (Element element : doc.select(".list-chapter li a")) {
                    Chapter chapter = new Chapter();
                    chapter.setName(element.attr("title"));
                    chapter.setUrl(element.attr("href"));
                    listChap.add(chapter);
                }

                return listChap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return listChap;
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
                Element titleElm = doc.select("a.chapter-title").first();
                chapter.setName(titleElm.attr("title"));
                chapter.setUrl(titleElm.attr("href"));

                contentChap.setChapter(chapter);
                Element prevElm = doc.select("#prev_chap").first();
                if (prevElm != null) {
                    contentChap.setPrevUrl(prevElm.attr("href"));
                }
                Element nextElm = doc.select("#next_chap").first();
                if (nextElm != null) {
                    contentChap.setNextUrl(nextElm.attr("href"));
                }

                contentChap.setContent(doc.select(".chapter-c").first().html());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentChap;
    }


    /**
     * Quick search;
     *
     * @param key key
     * @return List {@link Book};
     */
    public String quickSearch(String key) {
        String url = URL_PAGE + "ajax.php?type=quick_search&str=" + key + "&hash=" + getAjaxHashKey();
        System.out.println(url);
        try {
            Document doc = Jsoup.connect(url).get();
            if (doc != null) return doc.select("body").text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "No result!";
    }

    /**
     * Danh sach book tin kiem duoc;
     *
     * @param query tu khoa
     * @return list book;
     */
    public ArrayList<Book> searchBook(String query) {
        ArrayList<Book> listBooks = new ArrayList<>();
        try {
            String url = URL_PAGE + "/tim-kiem/?tukhoa=" + URLEncoder.encode(query, "UTF-8");
            Document doc = Jsoup.connect(url).get();
            System.out.println(url);
            if (doc != null) {
                Elements listElement = doc.select(".col-truyen-main .list-truyen > div.row");
                for (Element element : listElement) {
                    Book book = new Book();

                    book.setPoster(element.select("img.cover").attr("src"));

                    Element subjectElement = element.select("h3.truyen-title a").first();
                    book.setName(subjectElement.text());
                    book.setUrl(subjectElement.attr("href"));

                    book.setNew(element.select("span.label-new").first() != null);
                    book.setHot(element.select("span.label-hot").first() != null);
                    book.setFull(element.select("span.label-full").first() != null);

                    book.setAuthor(element.select("span.author").first().text());
                    book.setChapter(element.select("span.chapter-text").first().text());

                    listBooks.add(book);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listBooks;
    }

    /**
     * Get Book id;
     *
     * @param url link book;
     * @return book id;
     */
    public int getBookId(String url) {
        loadBookDocument(url);

        if (document != null) {
            return Integer.parseInt(document.getElementById("truyen-id").attr("value"));
        }
        return 0;
    }

    /**
     * Get total page chapter
     * @param url link book;
     * @return total page;
     */
    public int getBookTotalPageChapter(String url) {
        loadBookDocument(url);

        if (document != null) {
            return Integer.parseInt(document.getElementById("total-page").attr("value"));
        }
        return 0;
    }

    /**
     * Get hash key use load ajax;
     *
     * @return The hash key;
     */
    public String getAjaxHashKey() {
        if (hashKey != null && System.currentTimeMillis() - timeGetHash < EXPIRES_HASH) {
            return hashKey;
        }

        try {
            String url = URL_PAGE + "ajax.php?type=hash";
            Document doc = Jsoup.connect(url).get();
            if (doc != null) {
                hashKey = doc.select("body").text();
                timeGetHash = System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashKey;
    }


    /**
     * Get chapter content and url next chapter & prev chapter;
     *
     * @param url link chapter;
     * @return The HashMap chapter info : content => content chapter; next_chap => url next chapter;
     *          prev_chap => url previous chapter;
     */
    public Map<String, String> getChapterMapInfo(String url) {
        if (url == null || url.length() < 5) {
            return null;
        }

        try {
            Document doc = Jsoup.connect(url).get();
            if (doc != null) {
                HashMap<String, String> map = new HashMap<>();

                map.put("content", doc.getElementsByClass("chapter-c").first().html());

                Element nextChapElm = doc.getElementById("next_chap");
                if (!nextChapElm.hasClass("disabled")) {
                    map.put("next_chap", nextChapElm.attr("href"));
                }

                Element prevChapElm = doc.getElementById("prev_chap");
                if (!prevChapElm.hasClass("disabled")) {
                    map.put("prev_chap", prevChapElm.attr("href"));
                }

                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void showMsTest(String mes) {
        System.out.println(mes);
    }

}
