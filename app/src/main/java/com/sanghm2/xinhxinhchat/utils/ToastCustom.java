package com.sanghm2.xinhxinhchat.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastCustom {
    public static int t500s = 500;
    public static int t800s = 800;
    public static int t900s = 900;
    public static int t1000s = 1000;
    public static int t1500s = 1500;

    public static void showToast(Context context, String smg, int time) {
        final Toast toast = Toast.makeText(context, smg, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, time);
    }
}
