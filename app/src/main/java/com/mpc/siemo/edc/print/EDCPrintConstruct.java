package com.mpc.siemo.edc.print;

import com.imagpay.Settings;

public interface EDCPrintConstruct {
    interface Presenter {
        void print(int type);
    }

    interface View {
        void onPreparingReceipt(int type, Settings settings);
        void onPrintStarting();
        void isPrinting();
        void onPrintFinish(boolean response);
    }
}
