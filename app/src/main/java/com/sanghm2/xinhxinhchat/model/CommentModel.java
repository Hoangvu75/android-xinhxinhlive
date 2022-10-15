package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentModel {
    String createdTime, message;
    UserInfomation from;

    public static class UserInfomation {
        String id, name, avatar;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAvatar() {
            return avatar;
        }
    }

    public static List<CommentModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("data"), new TypeToken<List<CommentModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public String getMessage() {
        return message;
    }

    public UserInfomation getFrom() {
        return from;
    }

    public static CommentModel fromJsonSocket(String response) {
        Gson gson = new Gson();
        return gson.fromJson(response, new TypeToken<CommentModel>() {
        }.getType());
    }
}
