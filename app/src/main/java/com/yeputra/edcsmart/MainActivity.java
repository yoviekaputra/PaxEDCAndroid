package com.yeputra.edcsmart;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imagpay.MessageHandler;
import com.imagpay.PrnStrFormat;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.emv.EMVListener;
import com.imagpay.emv.EMVResponse;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.mpos.MposHandler;
import com.mpc.siemo.utils.AppUtils;
import com.mpc.siemo.utils.StringUtils;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SwipeListener, EMVListener {
    Button btPrint;
    TextView tvInfo;

    Settings settings;
    MposHandler handler;
    MessageHandler _mHandler;
    private boolean isPrint = false;
    private ProgressDialog dialog;
    private int showReadDailog = 101;
    private int showPrintDailog = 99;
    private int dismissDailog = 100;
    private int connectttl = 102;
    private boolean isconnect;

    Handler handleros = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == dismissDailog) {
                if (dialog != null)
                    dialog.dismiss();
            } else if (msg.what == showReadDailog) {
                dialog = PublicUtil.getDialog(MainActivity.this, "",
                        "reading card......");
                dialog.show();
            } else if (msg.what == showPrintDailog) {
                dialog = PublicUtil.getDialog(MainActivity.this, "",
                        "printting......");
                dialog.show();
            } else if (msg.what == connectttl) {
                try {
                    if (!handler.isConnected()) {
                        boolean hasconnect = handler.connect();
                        Thread.sleep(100);
                        isconnect = hasconnect;
                        MainActivity.this.sendMessage("Connect Res:"
                                + isconnect);
                    }
                } catch (Exception e) {
                }
            }
        }
    };
    public void sendMessage(String str) {
        _mHandler.sendMessage(str);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPrint = findViewById(R.id.btPrint);
        tvInfo  = findViewById(R.id.textView);
        btPrint.setOnClickListener(this);


        _mHandler = new MessageHandler(tvInfo);
        handleros.sendEmptyMessageDelayed(102, 900);
        handler = MposHandler.getInstance(this);
        handler.setShowLog(true);
        handler.setShowAPDU(true);
        handler.addSwipeListener(this);
        handler.addEMVListener(this);

        settings = Settings.getInstance(handler);
        // power on the device when you need to read card or print
        settings.mPosPowerOn();// Access to electricity
    }

    @Override
    public void onClick(View v) {
        if(isconnect)
            printTicket();
        else
            Toast.makeText(this,"Not connected",Toast.LENGTH_SHORT).show();
    }

    private void printTicket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isPrint)
                    return;
                isPrint = true;
                Message msg = new Message();
                msg.what = 99;
                handleros.sendMessage(msg);
                if (settings.isPrinting()) {// check print status
                    Log.d("", "setting.isPrinting():" + settings.isPrinting());
                    return;
                }
                StringBuffer receipts = new StringBuffer();

                /* HEADER */
                receipts.append("BANK NOBU\n");
                PrnStrFormat psf = new PrnStrFormat();
                psf.setTextSize(34);
                psf.setAli(Layout.Alignment.ALIGN_CENTER);
                settings.prnStr(receipts.toString(), psf);
                receipts.setLength(0);

                receipts.append("TERMID    : ABC\n");
                receipts.append("DATE TRX  : " + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()) +"\n");
                receipts.append("TRACE     : " + new SimpleDateFormat("HHmmss").format(new Date()) +"\n");
                receipts.append("CARD NUMB : 6274**********001 \n");
                receipts.append("=============================\n\n");
                psf.setTextSize(20);
                psf.setAli(Layout.Alignment.ALIGN_LEFT);
                settings.prnStr(receipts.toString(), psf);

                /* BODY */
                receipts.setLength(0);
                receipts.append("PEMBELIAN PULSA\n");
                receipts.append("OPERATOR  : SIMPATI\n");
                receipts.append("VOUCHER   : Rp.        25.000\n");
                receipts.append("REFNUM    : 0123456789ABCDEFG\n");
                receipts.append("=============================\n");
                psf.setAli(Layout.Alignment.ALIGN_LEFT);
                settings.prnStr(receipts.toString(),psf);

                /* FOOTER */
                receipts.setLength(0);
                receipts.append("TERIMAKASIH\n");
                receipts.append("TELAH MENGGUNAKAN EDC BANK NOBU\n");
                receipts.append("CALL CENTER 0123456\n\n\n");
                psf.setAli(Layout.Alignment.ALIGN_CENTER);
                settings.prnStr(receipts.toString(),psf);

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                opt.inPurgeable = true;
                opt.inInputShareable = true;
                @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(
                        R.drawable.ic_stat_name);
                Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
                settings.prnBitmap(bitmap);
                boolean res = settings.prnStart();
                Log.d("", "res:" + res);
            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        handler.onDestroy();// Close TTL
        settings.mPosPowerOff();
        settings.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy...", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    @Override
    public void onDisconnected(SwipeEvent swipeEvent) {

    }

    @Override
    public void onConnected(SwipeEvent swipeEvent) {
        sendMessage("Connect ok...");
        String ver = settings.readVersion();
        ver = StringUtils.hexToSting(ver);
        sendMessage("The version number:" + ver);
        sendMessage("app version:" + AppUtils.getVerName(getApplicationContext()));
    }




    @Override
    public void onParseData(SwipeEvent swipeEvent) {

    }

    @Override
    public void onCardDetect(CardDetected cardDetected) {

    }

    @Override
    public void onPrintStatus(PrintStatus status) {
        sendMessage("print status:" + status.toString());
        if (PrintStatus.EXIT.equals(status)) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isPrint = false;
            new Thread(new Runnable() {
                public void run() {
                    boolean resp = settings.printExitDetection();
                    if (resp) {
                        sendMessage("exit print............");
                    }
                    handleros.sendEmptyMessage(dismissDailog);
                }
            }).start();
        }
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
