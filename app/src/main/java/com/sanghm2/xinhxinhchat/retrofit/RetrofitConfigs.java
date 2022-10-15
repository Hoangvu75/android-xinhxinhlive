package com.sanghm2.xinhxinhchat.retrofit;

import android.util.Log;


import com.sanghm2.xinhxinhchat.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitConfigs {
    private static Retrofit retrofit = null;
    private static int TIMEOUT = 10;
    private static String TAG = "Sang";
    private static OkHttpClient okHttpClient = null;

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .build();
        return okHttpClient;
    }

    public static Retrofit getClient() {
        if (okHttpClient == null) {
            OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
            if (okHttpUtils != null && okHttpUtils.getmOkHttpClient() != null) {
                Log.e(TAG, "okHttpUtils 1");
                okHttpClient = okHttpUtils.getmOkHttpClient();
            } else {
                okHttpClient = getOkHttpClient();
                Log.e(TAG, "okHttpUtils 2");
            }
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.url_retrofit)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
