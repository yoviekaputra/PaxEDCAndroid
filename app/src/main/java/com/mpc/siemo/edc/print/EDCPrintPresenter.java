package com.mpc.siemo.edc.print;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.imagpay.enums.PrintStatus;
import com.mpc.siemo.edc.EDCBaseService;
import com.mpc.siemo.edc.EDCConfig;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public class EDCPrintPresenter extends EDCBaseService implements EDCPrintConstruct.Presenter {
    enum STATE{
        START(0),PREPARING(1),IS_PRINTING(2),FINISH(3);
        int value;

        STATE(int i) {value = i;}
        int getValue(){return value;}
    }

    private boolean isPrint = false;
    private EDCPrintConstruct.View view;

    public EDCPrintPresenter(final EDCPrintConstruct.View view, EDCConfig config){
        this.view = view;
        this.config = config;
        this.config.posHandler.addSwipeListener(this);
        this.config.osHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                if (msg.what == STATE.START.value) {
                    view.onPrintStarting();
                } else if(msg.what == STATE.PREPARING.value){
                    view.onPreparingReceipt(bundle.getInt("type"),EDCPrintPresenter.this.config.posSetting);
                }else if(msg.what == STATE.IS_PRINTING.value){
                    view.isPrinting();
                }else if(msg.what == STATE.FINISH.value){
                    view.onPrintFinish(bundle.getBoolean("response"));
                }
            }
        };
    }

    @Override
    public void print(final int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                config.osHandler.sendEmptyMessage(STATE.START.value);
                view.onPreparingReceipt(type,config.posSetting);

                if (isPrint || config.posSetting.isPrinting()) {// check print status
                    Log.d("", "Setting.isPrinting():" + config.posSetting.isPrinting());
                    config.osHandler.sendEmptyMessage(STATE.IS_PRINTING.value);
                    return;
                }
                isPrint = true;
                boolean res = config.posSetting.prnStart();
                Log.d(TAG, "Print start status : " + res);
            }
        }).start();
    }

    @Override
    public void onPrintStatus(PrintStatus printStatus) {
        super.onPrintStatus(printStatus);

        if (PrintStatus.EXIT == printStatus) {
            Log.d(TAG,"Catch exit print status...");
            isPrint = false;
            new Thread(new Runnable() {
                public void run() {
                    boolean resp = config.posSetting.mPosConfirmExitPrint();
                    if(resp){
                        sendViewLogging("exit print............");
                    }
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("response",resp);
                    config.osHandler.sendMessage(getMessage(STATE.FINISH,bundle));
                    Log.d(TAG,"Sending response status printer to view...");
                }
            }).start();
        }
    }

    private Message getMessage(STATE state, Bundle bundle){
        Message msg = new Message();
        msg.what = state.value;
        msg.setData(bundle);
        return msg;
    }
}
