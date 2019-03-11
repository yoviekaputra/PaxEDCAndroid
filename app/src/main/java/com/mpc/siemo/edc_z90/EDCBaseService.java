package com.mpc.siemo.edc_z90;

import android.util.Log;

import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.emv.EMVListener;
import com.imagpay.emv.EMVResponse;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.mpc.siemo.utils.AppUtils;
import com.mpc.siemo.utils.StringUtils;

import java.util.List;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public abstract class EDCBaseService implements SwipeListener, EMVListener {
    protected String TAG = getClass().getSimpleName();
    protected EDCConfig config = new EDCConfig();

    @Override
    public void onDisconnected(SwipeEvent swipeEvent) {

    }

    @Override
    public void onConnected(SwipeEvent swipeEvent) {
        if(config.viewLogger != null){
            sendViewLogging("Connect ok...");
            String ver = config.posSetting.readVersion();
            ver = StringUtils.hexToSting(ver);
            sendViewLogging("The version number: " + ver);
            sendViewLogging("app version: " + AppUtils.getVerName(config.context));
        }
    }

    public void sendViewLogging(String msg){
        if(config.viewLogger != null){
            config.viewLogger.sendMessage(msg);
        }
    }
    public boolean isConnected(){
        if(config.posHandler!=null){
            return config.posHandler.isConnected();
        }
        return false;
    }

    @Override
    public void onParseData(SwipeEvent swipeEvent) {
        Log.v(TAG,"Parser data : " + swipeEvent.getValue());
        sendViewLogging("Parser data : " + swipeEvent.getValue());
    }

    @Override
    public void onCardDetect(CardDetected cardDetected) {
        Log.v(TAG,"Card detected : " + cardDetected);
        sendViewLogging("Card detected : " + cardDetected);
    }

    @Override
    public void onPrintStatus(PrintStatus printStatus) {
        Log.v(TAG,"Print status : " + printStatus);
        sendViewLogging("Print status : " + printStatus);
    }

    @Override
    public void onEmvStatus(EmvStatus emvStatus) {
        Log.v(TAG,"EMV status : " + emvStatus);
        sendViewLogging("EMV status : " + emvStatus);
    }

    @Override
    public int onSelectApp(List<String> list) {
        return 0;
    }

    @Override
    public boolean onReadData() {
        return false;
    }

    @Override
    public String onReadPin(int i, int i1) {
        return null;
    }

    @Override
    public EMVResponse onSubmitData() {
        return null;
    }

    @Override
    public void onConfirmData() {

    }

    @Override
    public void onReversalData() {

    }
}
