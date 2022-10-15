package com.sanghm2.xinhxinhchat.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileModel {
    private final UserInfo userInfo;
    private final int followTotal;
    private final int followerTotal;
    private final int likeTotal;
    private final Campaign campaign;

    public ProfileModel(UserInfo userInfo, int followTotal, int followerTotal, int likeTotal, Campaign campaign) {
        this.userInfo = userInfo;
        this.followTotal = followTotal;
        this.followerTotal = followerTotal;
        this.likeTotal = likeTotal;
        this.campaign = campaign;
    }

    public static ProfileModel FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(String.valueOf(jsonData), new TypeToken<ProfileModel>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ProfileModel(null, 0, 0, 0, null);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public int getFollowTotal() {
        return followTotal;
    }

    public int getFollowerTotal() {
        return followerTotal;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public int getLikeTotal() {
        return likeTotal;
    }

    public static class UserInfo {
        private final String shopName;
        private final String phone;
        private final String avatar;

        public UserInfo(String shopName, String phone, String avatar) {
            this.shopName = shopName;
            this.phone = phone;
            this.avatar = avatar;
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
    }

    public static class Campaign {
        private final String total;

        public Campaign(String total) {
            this.total = total;
        }

        public String getTotal() {
            return total;
        }
    }

}
