package com.yeputra.edcsmart;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.basewin.database.DataBaseManager;
import com.basewin.log.LogUtil;
import com.basewin.services.ServiceManager;

public class AppContext extends Application {
    private static final String TAG = "DemoApplication";
    private static AppContext instance;

    public static AppContext getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try{
            /**
             * init Device Server
             */
//            ServiceManager.getInstence().init(instance);
            /**
             * init database
             */
            // DataBaseManager.getInstance().init(getApplicationContext());
  //          LogUtil.openLog();
        }catch (Exception e){
            Toast.makeText(instance,"" + e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }
}
