package com.yeputra.edcsmart;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.services.ServiceManager;
import com.imagpay.PrnStrFormat;
import com.imagpay.Settings;
import com.mpc.siemo.edc_z90.EDCSmartFactory;
import com.mpc.siemo.edc_z90.print.EDCPrintConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements android.view.View.OnClickListener, EDCPrintConstruct.View {
    Button btPrint;
    TextView tvInfo;

    private ProgressDialog dialog;
    private EDCSmartFactory edcService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPrint = findViewById(R.id.btPrint);
        tvInfo  = findViewById(R.id.textView);
        btPrint.setOnClickListener(this);
        tvInfo.setOnClickListener(this);

        edcService = EDCSmartFactory.getInstance(this);
        edcService.setLoggerView(tvInfo);
        edcService.onStart();
    }

    @Override
    public void onClick(android.view.View v) {
        switch (v.getId()){
            case R.id.btPrint:
                if(edcService.isConnected()){
                    edcService.getPrinter(this).print(0);
                }else {
                    printText();
                    Toast.makeText(this,"TTL not connected",Toast.LENGTH_SHORT).show();
                }
            case R.id.textView:
                rxJava2();
        }

    }

    public void rxJava2(){
        final List<String> alphabets = getAlphabetList();

        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) {
                try {
                    for (String alphabet : alphabets) {
                        emitter.onNext(alphabet);
                        Thread.sleep(1000);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });

        Observer observer = new Observer() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe");
                onPrintStarting();
                setTextDialog("onSubcribe");
            }

            @Override
            public void onNext(Object o) {
                System.out.println("onNext: " + o);
                setTextDialog("onNext : " + o);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError: " + e.getMessage());
                setTextDialog(e.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
                onPrintFinish(true);
            }
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private List<String> getAlphabetList() {
        List<String> a = new ArrayList<>();
        a.add("A");
        a.add("B");
        a.add("C");
        a.add("D");
        a.add("E");
        a.add("F");
        a.add("H");
        a.add("I");
        a.add("J");
        return a;
    }

    @SuppressLint("CheckResult")
    public void printText() {
        // add text printer
        JSONObject json1 = new JSONObject();
        StringBuffer receipts = new StringBuffer();
        /* HEADER */
        receipts.append("BANK NOBU\n");
        receipts.append("TERMID    : ABC\n");
        receipts.append("DATE TRX  : " + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()) +"\n");
        receipts.append("TRACE     : " + new SimpleDateFormat("HHmmss").format(new Date()) +"\n");
        receipts.append("CARD NUMB : 6274**********001 \n");
        receipts.append("=============================\n");

        /* BODY */
        receipts.append("PEMBELIAN PULSA\n");
        receipts.append("OPERATOR  : SIMPATI\n");
        receipts.append("VOUCHER   : Rp.        25.000\n");
        receipts.append("REFNUM    : 0123456789ABCDEFG\n");
        receipts.append("=============================\n");

        /* FOOTER */
        receipts.append("TERIMAKASIH\n");
        receipts.append("TELAH MENGGUNAKAN EDC BANK NOBU\n");
        receipts.append("CALL CENTER 0123456\n\n\n\n\n");

        JSONArray printTest = new JSONArray();
        final JSONObject printJson = new JSONObject();
        try{
            // Add text printing
            json1.put("content-type", "txt");
            json1.put("content", receipts);
            json1.put("size", "20");
            json1.put("position", "left");
            json1.put("offset", "0");
            json1.put("bold", "0");
            json1.put("italic", "0");
            json1.put("height", "-1");

            ServiceManager.getInstence().getPrinter().setPrintGray(2000);// set
            // Gray
            ServiceManager.getInstence().getPrinter().setLineSpace(2);// set
            // lineSpace

            printTest.put(json1);

            printJson.put("spos", printTest);

            ServiceManager.getInstence().getPrinter().print(printJson.toString(), null, new OnPrinterListener() {
                @Override
                public void onError(int i, String s) {
                    onPrintFinish(false);
                }

                @Override
                public void onFinish() {
                    onPrintFinish(true);
                }

                @Override
                public void onStart() {
                    onPrintStarting();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if(edcService != null)
            edcService.onStop();
        Toast.makeText(getApplicationContext(), "onDestroy...", Toast.LENGTH_SHORT).show();
        super.onDestroy();

    }

    @Override
    public void onPreparingReceipt(int type, Settings settings) {
        Log.d("","Preparing receipt");
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
        receipts.append("=============================\n");
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
        receipts.append("CALL CENTER 0123456\n\n");
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
        receipts.setLength(0);
        receipts.append("\n\n");
        settings.prnStr(receipts.toString(),psf);

        Log.d("","Finish prepare receipt");
    }

    @Override
    public void onPrintStarting() {
        dialog = PublicUtil.getDialog(MainActivity.this,"","Print starting....");
        dialog.show();
    }

    public void setTextDialog(String msg){
        if(dialog != null && dialog.isShowing()){
            dialog.setMessage(msg);
        }
    }
    @Override
    public void isPrinting() {
        onPrintFinish(true);
        Toast.makeText(AppContext.getInstance(),"Printer is printing",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrintFinish(boolean response) {
        if(dialog != null)
            dialog.dismiss();
    }
}
