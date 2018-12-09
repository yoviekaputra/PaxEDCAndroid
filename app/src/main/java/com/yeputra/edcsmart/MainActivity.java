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

import com.imagpay.PrnStrFormat;
import com.imagpay.Settings;
import com.mpc.siemo.edc.EDCSmartFactory;
import com.mpc.siemo.edc.print.EDCPrintConstruct;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        edcService = EDCSmartFactory.getInstance(this);
        edcService.setLoggerView(tvInfo);
        edcService.onStart();
    }

    @Override
    public void onClick(android.view.View v) {
        if(edcService.isConnected()){
            edcService.getPrinter(this).print(0);
        }else{
            Toast.makeText(this,"TTL not connected",Toast.LENGTH_SHORT).show();
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

    @Override
    public void isPrinting() {
        onPrintFinish(true);
        Toast.makeText(MainActivity.this,"Presenter is printing",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrintFinish(boolean response) {
        if(dialog != null)
            dialog.dismiss();
    }
}
