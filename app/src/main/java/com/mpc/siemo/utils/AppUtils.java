package com.mpc.siemo.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public class AppUtils {
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
