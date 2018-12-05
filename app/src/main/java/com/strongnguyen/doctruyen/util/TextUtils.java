package com.strongnguyen.doctruyen.util;

import android.os.Build;
import android.text.Html;

public class TextUtils {

    @SuppressWarnings("deprecation")
    public static CharSequence styleTextHtml(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(s);
        }
    }
    
    public static String boLocTu1(String str) {
        if (str == null || str.length() < 3) return str;

        String s = str;
        s = s.replace("A đù", "A");
        s = s.replace("A... đù", "A");
        s = s.replace("Á đù", "Á");
        s = s.replace("Á đù!", "Á!");
        s = s.replace("Á... đù!", "Á!");
        s = s.replace("ơ đù", "ơ");
        s = s.replace("ơ... đù", "ơ");
        s = s.replace("thông ass", "giết chết");
        s = s.replace("thông át", "giết chết");
        s = s.replace("thông nát át", "giết chết");
        s = s.replace("thông nát ass", "giết chết");
        s = s.replace("thông đít", "giết chết");
        s = s.replace("thông xong", "giết xong");
        s = s.replace("thông được", "giết được");
        s = s.replace("thông nhau", "giết nhau");
        s = s.replace("thông chết", "giết chết");
        s = s.replace("thông ngươi", "giết ngươi");
        s = s.replace("thông một", "giết một");
        s = s.replace("thông rơi", "đập rơi");
        s = s.replace("quần xì líp", "váy");
        s = s.replace("xì líp", "áo");
        s = s.replace("đâu nè", "");
        s = s.replace("nè", "");
        s = s.replace("(", "");
        s = s.replace(")", "");
        //s = s.replace("mông", "vai");
        //s = s.replace("chim ", "vai ");
        s = s.replace("mênh vai", "mênh mông");
        s = s.replace("vai lung", "mông lung");
        s = s.replace("--", "");
        s = s.replace("Nguồn truyện:", "");
        s = s.replace("www.Truyện FULL", "");
        s = s.replace("Truyện FULL", "");
        s = s.replace("Bạn đang đọc truyện được copy tại", "");
        s = s.replace("Bạn đang đọc truyện được lấy tại chấm cơm.", "");
        s = s.replace("Bạn đang đọc truyện tại", "");
        s = s.replace("http://truyenfull.vn", "");
        s = s.replace("Truyện được biên tập. tại", "");
        s = s.replace("Truyện được biên tập tại", "");
        s = s.replace("Truyện được biên tập", "");
        s = s.replace("ire.a.d.vn", "");
        
        return s;
    }

    public static String boLocTu2(String str) {
        if (str == null || str.length() < 3) return str;

        String s = str;
        s = s.replace("thượng tuyến", "online");
        s = s.replace("tại tuyến", "online");
        s = s.replace("Ngọa thảo", "Đậu xanh");
        s = s.replace("ngọa thảo", "đậu xanh");
        s = s.replace("dược hoàn", "thảm");
        s = s.replace("Dược hoàn", "thảm");
        s = s.replace("dược hoàn.", "thảm");
        s = s.replace("Dược hoàn.", "thảm");
        s = s.replace("Mời bạn đón đọc siêu phẩm", "");
        s = s.replace("motip mới lạ, không não tàn, không YY và đặc biệt là hay tuyệt.", "");
        s = s.replace("main bá, lãnh huyết không não tàn và có khả năng mở hộp rất bá", "");
        s = s.replace("Tống Thư Hàng đạo", "Tống Thư Hàng nói");
        s = s.replace("Tống Thư Hàng đạo:", "Tống Thư Hàng nói:");
        s = s.replace("thập lục đạo", "thập lục nói");
        s = s.replace("nhếch", " ");
        s = s.replace("trẻ con", "tiểu tử");
        s = s.replace("Trẻ con", "Tiểu tử");
        s = s.replace("điện bên trong", "bên trong điện");
        s = s.replace("công bên dưới chủ điện", "công chúa điện hạ");
        s = s.replace("http: //www. uukanshu. com", " ");
        s = s.replace("http://www.uukanshu.com", " ");
        s = s.replace("http://www.uukanshu.com", " ");
        return s;
    }

    public static String boLocTu3(String str) {
        if (str == null || str.length() < 3) return str;

        String s = str;
        s = s.replace("đạo", "nói");
        s = s.replace("nói hữu", "đạo hữu");
        s = s.replace("nói hiệu", "đạo hiệu");
        s = s.replace("nói lý", "đạo lý");
        s = s.replace("nói trưởng", "đạo trưởng");
        s = s.replace("nói Thạch", "đạo Thạch");
        s = s.replace("Nói Thạch", "Đạo Thạch");
        s = s.replace("nói hư", "đạo hư");
        s = s.replace("nói bào", "đạo bào");
        s = s.replace("nói đạo", "bần đạo");
        s = s.replace("quan nói", "quan đạo");

        return s;
    }

    public static String boLocTu4(String str) {
        if (str == null || str.length() < 3) return str;
        String s = str;
        s = s.replace("**", " ");
        s = s.replace("×××", "×");
        s = s.replace("xxxx", "×");
        s = s.replace("~", "");
        s = s.replace("(", "");
        s = s.replace(")", "");
        return s;
    }

    public static String boLocTa1979(String str) {
        if (str == null || str.length() < 3) return str;
        String s = str;
        s = s.replace("đạo", "nói");
        return s;
    }

}
