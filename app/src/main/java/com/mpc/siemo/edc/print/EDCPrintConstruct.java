package com.mpc.siemo.edc.print;

import com.imagpay.Settings;

public interface EDCPrintConstruct {
    interface System{
        void print(Settings setting);
    }

    interface Presenter{
        void onStart();
        void isPrinting();
        void onFinish(boolean response);
    }
}
