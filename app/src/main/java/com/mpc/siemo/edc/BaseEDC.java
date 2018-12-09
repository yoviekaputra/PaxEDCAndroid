package com.mpc.siemo.edc;

import android.content.Context;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.emv.EMVListener;
import com.imagpay.emv.EMVResponse;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.mpos.MposHandler;

import java.util.List;
import java.util.logging.Logger;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public abstract class BaseEDC implements SwipeListener, EMVListener {
    protected Context context;
    protected MposHandler posHandler;
    protected Settings posSetting;
    protected MessageHandler viewLogger;

    @Override
    public void onDisconnected(SwipeEvent swipeEvent) {

    }

    @Override
    public void onConnected(SwipeEvent swipeEvent) {

    }

    @Override
    public void onParseData(SwipeEvent swipeEvent) {

    }

    @Override
    public void onCardDetect(CardDetected cardDetected) {

    }

    @Override
    public void onPrintStatus(PrintStatus printStatus) {

    }

    @Override
    public void onEmvStatus(EmvStatus emvStatus) {

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
