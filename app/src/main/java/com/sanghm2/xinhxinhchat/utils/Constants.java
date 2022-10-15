package com.sanghm2.xinhxinhchat.utils;

import androidx.multidex.BuildConfig;

public class Constants {
    //key
    public static final String ID = "_id";
    public static final String TOKEN = "TOKEN";
    public static final String PHONE_NUMBER = "phone";
    public static final String SHOP_NAME = "shopName";
    public static final String TYPE = "type";
    public static final String STATUS = "status";
    public static final String VERIFIED = "verified";
    public static final String PROFILE = "XinhXinhProfile";
    public static final String PAGE_ID = "pageId";
    //socket
    public static final String AUTHENTICATED = "authenticated_success";
    public static final String COMMENT_FB = "comment_fb_campaign";
    public static final String ORDER_CAMPAIGN = "order_xx_campaign";
    public static final String REACTION_CAMPAIGN = "reaction_xx_campaign";
    public static final String STATUS_CAMPAIGN = "status_xx_campaign";
    public static final String VIEW_CAMPAIGN = "view_xx_campaign";

    public static final String LEAVE_CAMPAIGN = "leave_campaign";
    public static final String REQUEST_CAMPAIGN = "request_campaign";
    //url
    public static String KEYDEV = "dev";
    public static String KEYSTG = "stg";
    public static String KEYPRO = "release";

    public static String url_socket = ConfigUrl.getUrlSocket();
    public static String url_retrofit = ConfigUrl.getUrlRetrofit();

    public static class ConfigUrl {
        public static String getUrlSocket() {
            if (BuildConfig.BUILD_TYPE.equals(KEYPRO)) {
                //pro
                return "https://socket-prod.xinhxinh.live";
            } else if (BuildConfig.BUILD_TYPE.equals(KEYDEV)) {
                //dev
                return "https://dev-socket.xinhxinh.live";
            } else {
                //stg
                return "https://socket.stg.xinhxinh.live";
            }
        }

        public static String getUrlRetrofit() {
            if (BuildConfig.BUILD_TYPE.equals(KEYPRO)) {
                //pro
                return "https://prod.xinhxinh.live";
            } else if (BuildConfig.BUILD_TYPE.equals(KEYDEV)) {
                //dev
                return "https://dev.xinhxinh.live";
            } else {
                //stg
                return "https://stg.xinhxinh.live";
            }
        }
    }
}
