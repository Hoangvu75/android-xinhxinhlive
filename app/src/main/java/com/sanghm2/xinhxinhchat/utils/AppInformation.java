package com.sanghm2.xinhxinhchat.utils;

import android.content.Context;
import android.provider.Settings;

import com.sanghm2.xinhxinhchat.BuildConfig;

public class AppInformation {
    public static int mVersionCode = BuildConfig.VERSION_CODE;

    public static String mVersionName = BuildConfig.VERSION_NAME;

    public static String mPlatform = "Android";

    public static String getVersion = mPlatform + "/" + mVersionName;

    public static String getUUID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
