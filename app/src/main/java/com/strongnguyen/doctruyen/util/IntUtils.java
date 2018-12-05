package com.strongnguyen.doctruyen.util;

/**
 * Content class.
 * <p>
 * Created by Mr Cuong on 9/10/2017.
 * Email: vancuong2941989@gmail.com
 */

public class IntUtils {

    public static int convertStringToInt(String str) {
        if (str == null || str.length() == 0) return 0;

        str = str.replaceAll("[^0-9]", "");
        if (str.length() == 0) return 0;

        return Integer.parseInt(str);
    }
}
