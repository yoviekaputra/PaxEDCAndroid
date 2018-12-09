package com.mpc.siemo.utils;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public class StringUtils {
    public static String hexToSting(String ver) {
        if (ver == null)
            return "";
        String[] tmps = ver.trim().replaceAll("..", "$0 ").split(" ");
        StringBuffer sbf = new StringBuffer();
        for (String str : tmps) {
            sbf.append((char) Integer.parseInt(str, 16));
        }
        ver = sbf.toString();
        return ver;
    }
}
