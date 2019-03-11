package com.mpc.siemo.edc_z90;

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

                    try{
                        posSetting.mPosPowerOn();
                    }catch(Exception e){
                        Log.e(TAG,"Electronic to power on failed!");
                        viewLogger.sendMessage("Electronic to power on failed!");
                        return;
                    }

                    try{
                        posHandler.connect();
                    }catch(Exception e){
                        Log.e(TAG,"Connection failed!");
                        viewLogger.sendMessage("Connection failed!");
                        return;
                    }

                    Log.i(TAG,"Initialize success...");
                    viewLogger.sendMessage("Initialize success");
                }
            },1000);
        }
    }
    public void clear(){
        if(posHandler.isConnected()){
            this.posHandler.onDestroy();
        }
        this.posSetting.mPosPowerOff();
        this.posSetting.onDestroy();
        viewLogger.sendMessage("EDCSmart configure is cleared");
    }
}
