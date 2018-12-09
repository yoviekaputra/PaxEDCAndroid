package com.mpc.siemo.edc.print;

import android.util.Log;
import com.imagpay.Settings;
import com.imagpay.enums.PrintStatus;
import com.mpc.siemo.edc.BaseEDC;

/***
 * @author yovi.putra
 * 09-Dec-2018
 */
public class EDCPrint extends BaseEDC implements EDCPrintConstruct.System {
    private boolean isPrint = false;
    private EDCPrintConstruct.Presenter presenter;

    public EDCPrint(EDCPrintConstruct.Presenter presenter){
        this.presenter = presenter;
    }

    @Override
    public void print(final Settings settings){
        new Thread(new Runnable() {
            @Override
            public void run() {
                presenter.onStart();
                if (isPrint || settings.isPrinting()) {// check print status
                    Log.d("", "Setting.isPrinting():" + settings.isPrinting());
                    presenter.isPrinting();
                    return;
                }
                isPrint = true;
                boolean res = settings.prnStart();
                Log.d("", "res:" + res);
            }
        }).start();
    }

    @Override
    public void onPrintStatus(PrintStatus printStatus) {
        if (PrintStatus.EXIT.equals(printStatus)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isPrint = false;
            new Thread(new Runnable() {
                public void run() {
                    boolean resp = posSetting.mPosConfirmExitPrint();
                    presenter.onFinish(resp);
                }
            }).start();
        }
    }
}
