package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlockUserModel {
    private String shopID;
    private String shopName;
    private String avatar;
    private String status;
    private String createdAt;

    public BlockUserModel(String shopID, String shopName, String avatar, String status, String createdAt) {
        this.shopID = shopID;
        this.shopName = shopName;
        this.avatar = avatar;
        this.status = status;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BlockUserModel{" +
                "shopID='" + shopID + '\'' +
                ", shopName='" + shopName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }

    public static List<BlockUserModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("list"), new TypeToken<List<BlockUserModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
