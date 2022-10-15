package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginDataModel {
    private final String accessToken;
    private final User user;

    public LoginDataModel(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public User getUser() {
        return user;
    }

    public static LoginDataModel FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(String.valueOf(jsonData), new TypeToken<LoginDataModel>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new LoginDataModel(null, null);
    }

    @Override
    public String toString() {
        return "LoginDataModel{" +
                "accessToken='" + accessToken + '\'' +
                ", user=" + user +
                '}';
    }

    static class User {
        private final String _id;
        private final String phone;
        private final String shopName;

        public User(String _id, String phone, String shopName) {
            this._id = _id;
            this.phone = phone;
            this.shopName = shopName;
        }

        public String get_id() {
            return _id;
        }

        public String getPhone() {
            return phone;
        }

        public String getShopName() {
            return shopName;
        }
    }
}