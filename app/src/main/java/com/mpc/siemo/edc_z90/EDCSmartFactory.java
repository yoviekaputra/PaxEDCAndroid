package com.mpc.siemo.edc_z90;

import android.content.Context;
import android.widget.TextView;

import com.imagpay.MessageHandler;
import com.imagpay.Settings;
import com.mpc.siemo.edc_z90.print.EDCPrintPresenter;
import com.mpc.siemo.edc_z90.print.EDCPrintConstruct;

/***
 * @author yovi.putra
 * 20181209
 */
public class EDCSmartFactory extends EDCBaseService implements EDCConstruct, EDCConstruct.onViewLogging{
    private final String TAG = getClass().getSimpleName();
    private static EDCSmartFactory instance = null;
    private EDCPrintPresenter instanceEdcPrint = null;

    private EDCSmartFactory(Context context){
        this.config.context = context;
    }

    /***
     * @param context
     * @return
     */
    public static EDCSmartFactory getInstance(Context context){
        if(instance == null){
            synchronized (EDCSmartFactory.class){
                if(instance == null){
                    instance = new EDCSmartFactory(context);
                }
            }
        }
        return instance;
    }

    public EDCPrintPresenter getPrinter(EDCPrintConstruct.View view){
        if(instanceEdcPrint == null){
            synchronized (EDCPrintPresenter.class){
                if(instanceEdcPrint == null){
                    instanceEdcPrint = new EDCPrintPresenter(view, config);
                }
            }
        }
        return instanceEdcPrint;
    }

    @Override
    public void onStart() {
        this.config.initialize();
    }

    @Override
    public void onStop() {
       this.config.clear();
    }

    @Override
    public Settings getSetting() {
        return this.config.posSetting;
    }

    @Override
    public void setLoggerView(TextView view) {
        this.config.viewLogger = new MessageHandler(view);
    }
}
