package com.sanghm2.xinhxinhchat.retrofit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonElement;
import com.sanghm2.xinhxinhchat.utils.XinhXinhPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Response;

public class ErrorBody {
    public static String getErrorMessage(Response<JsonElement> response) {
        try {
            String error = response.errorBody().string();
            JSONObject jsonObject = new JSONObject(error);
            return jsonObject.getString("message");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void Error401(Context context, Response<JsonElement> response) {
        if (response.code() == 401) {
            XinhXinhPref.removeAll(context);
//            Intent intent = new Intent(context, Loginctivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            context.startActivity(intent);
        }
    }
}
