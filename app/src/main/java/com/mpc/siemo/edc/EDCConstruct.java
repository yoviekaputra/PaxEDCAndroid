package com.mpc.siemo.edc;

import android.content.Context;
import android.widget.TextView;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public interface EDCConstruct {
    interface View{
        void onLoading(String message);
        void onHiddenLoading();
    }

    interface Presenter{
        void onStart();

        void onStop();

        interface onViewLogging{
            void setView(TextView view);
            void onListener(String message);
        }
    }
}
