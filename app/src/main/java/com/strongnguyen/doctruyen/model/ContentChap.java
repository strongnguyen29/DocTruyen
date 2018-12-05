package com.strongnguyen.doctruyen.model;

import com.strongnguyen.doctruyen.util.TextUtils;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 5/26/2018.
 * Email: vancuong2941989@gmail.com
 */
public class ContentChap {
    private Chapter chapter;
    private String content;
    private String nextUrl;
    private String prevUrl;

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public String getContent() {
        if (content == null) return "";
        return content;
    }

    public void setContent(String content) {
        this.content = TextUtils.boLocTu1(content);
        this.content = TextUtils.boLocTu2(this.content);
        //this.content = TextUtils.boLocTu3(this.content);
        this.content = TextUtils.boLocTu4(this.content);
    }

    public String getNextUrl() {
        if (nextUrl == null) return "";
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = nextUrl;
    }

    public String getPrevUrl() {
        if (prevUrl == null) return "";
        return prevUrl;
    }

    public void setPrevUrl(String prevUrl) {
        this.prevUrl = prevUrl;
    }
}
