package com.sanghm2.xinhxinhchat.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class ThemeHelper {
    public static void setFullScreen(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static void setColorSatusbar(Window window, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    public static void dialogFullScreen(Window window){
        final WindowManager.LayoutParams wlmp = window.getAttributes();
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlmp);
    }

    public static void dialogWrapContent(Window window){
        final WindowManager.LayoutParams wlmp = window.getAttributes();
        wlmp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlmp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlmp.gravity = Gravity.BOTTOM;
        wlmp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlmp);
    }
}
