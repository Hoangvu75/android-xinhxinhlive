package com.sanghm2.xinhxinhchat.retrofit;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class OkHttpUtils {
    private  final int TIMEOUT = 10;
    private  OkHttpClient mOkHttpClient;
    private static OkHttpUtils INSTANCE;

    public static OkHttpUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OkHttpUtils();
        }
        return INSTANCE;
    }

    public OkHttpUtils() {
        init();
    }

    public void init(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT * 5, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                });
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        mOkHttpClient = builder.build();
    }

    public OkHttpClient getmOkHttpClient() {
        return mOkHttpClient;
    }

    public void setmOkHttpClient(OkHttpClient mOkHttpClient) {
        this.mOkHttpClient = mOkHttpClient;
    }
}
