package com.mpc.siemo.edc;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.mpos.MposHandler;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public class EDCConfig {
    private String TAG = getClass().getSimpleName();
    public Context context;
    public MposHandler posHandler;
    public Settings posSetting;
    public MessageHandler viewLogger;
    public Handler osHandler;

    public void initialize(){
        if(posHandler == null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    posHandler = MposHandler.getInstance(context);
                    posHandler.setShowLog(true);
                    posHandler.setShowAPDU(true);
                    posSetting = Settings.getInstance(posHandler);

                    posHandler.connect();
                    posSetting.mPosPowerOn();
                    Log.i(TAG,"Initialize success...");
                }
            },1000);
        }
    }
    public void clear(){
        this.posHandler.onDestroy();
        this.posSetting.mPosPowerOff();
        this.posSetting.onDestroy();
    }
}
