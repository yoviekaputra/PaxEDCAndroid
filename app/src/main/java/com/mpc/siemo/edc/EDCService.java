package com.mpc.siemo.edc;

import android.content.Context;
import android.widget.TextView;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.imagpay.SwipeListener;
import com.imagpay.emv.EMVListener;
import com.imagpay.mpos.MposHandler;

/***
 * @author yovi.putra
 * 20181209
 */
public class EDCService extends BaseEDC implements EDCConstruct.Presenter, EDCConstruct.Presenter.onViewLogging{
    private static EDCService instance = null;

    private EDCService(Context context){
        this.context = context;
    }

    /***
     * @param context
     * @return
     */
    public static EDCService getInstance(Context context){
        if(instance == null){
            synchronized (EDCService.class){
                if(instance == null){
                    instance = new EDCService(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onStart() {
        this.posHandler = MposHandler.getInstance(this.context);
        this.posHandler.setShowLog(true);
        this.posHandler.setShowAPDU(true);
        this.posHandler.addSwipeListener(this);
        this.posHandler.addEMVListener(this);

        this.posSetting = Settings.getInstance(this.posHandler);
        this.posSetting.mPosPowerOn();
    }

    @Override
    public void onStop() {
        this.posHandler.onDestroy();
        this.posSetting.mPosPowerOff();
        this.posSetting.onDestroy();
    }

    @Override
    public void setView(TextView view) {
        viewLogger = new MessageHandler(view);
    }

    @Override
    public void onListener(String message) {
        if(viewLogger != null){
            viewLogger.sendMessage(message);
        }
    }
}
