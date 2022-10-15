package com.sanghm2.xinhxinhchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

public class XinhXinhPref {
    static final String SharedPreferences = "XinhXinhPref";

    public static void setStringPreference(Context context,String key, String input) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,input);
        editor.apply();
    }

    public static String getStringPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static boolean getBooleanPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferences, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
