package com.mpc.siemo.edc;

import android.content.Context;
import android.widget.TextView;

import com.imagpay.Settings;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public interface EDCConstruct {
    void onStart();

    void onStop();

    Settings getSetting();

    interface onViewLogging{
        void setLoggerView(TextView view);
    }
}
