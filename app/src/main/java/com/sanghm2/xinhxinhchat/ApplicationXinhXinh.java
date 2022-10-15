package com.sanghm2.xinhxinhchat;

import android.app.Application;

import androidx.multidex.MultiDex;

public class ApplicationXinhXinh extends Application {

    public static Application mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //init resize image
        MultiDex.install(this);
    }
}
