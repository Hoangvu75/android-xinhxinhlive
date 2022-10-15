package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchShopModel {
    private String _id;
    private String shopName;
    private String phone;
    private String avatar;
    private String fullName;

    public static List<SearchShopModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("data"), new TypeToken<List<SearchShopModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public String getShopName() {
        return shopName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFullName() {
        return fullName;
    }
}
