package com.sanghm2.xinhxinhchat.model;

import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BroadcastModel implements Serializable {
    String _id;
    long countReactions;
    long maxCountViews;
    String title;
    String thumbnail;
    String status;
    List<ListLive> listLive;
    String linkVideo;
    String shopName;
    String avatarOwner;
    String shopID;
    String description;
    long startTime;

    public static List<BroadcastModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data").getJSONObject("newest");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("data"), new TypeToken<List<BroadcastModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<BroadcastModel> FromJson2(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("results"), new TypeToken<List<BroadcastModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<BroadcastModel> FromJson3(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data").getJSONObject("trending");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("data"), new TypeToken<List<BroadcastModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String get_id() {
        return _id;
    }

    public long getCountReactions() {
        return countReactions;
    }

    public long getMaxCountViews() {
        return maxCountViews;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getStatus() {
        return status;
    }

    public List<ListLive> getListLive() {
        return listLive;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAvatarOwner() {
        return avatarOwner;
    }

    public String getShopID() {
        return shopID;
    }

    public String getDescription() {
        return description;
    }

    public long getStartTime() {
        return startTime;
    }

    public static class ListLive implements Serializable {
        String _id;
        String linkLive;
        String objectType;
        String linkVideo;
        String linkFacebook;

        protected ListLive(Parcel in) {
            _id = in.readString();
            linkLive = in.readString();
            objectType = in.readString();
            linkVideo = in.readString();
            linkFacebook = in.readString();
        }

        public String get_id() {
            return _id;
        }

        public String getLinkLive() {
            return linkLive;
        }

        public String getObjectType() {
            return objectType;
        }

        public String getLinkVideo() {
            return linkVideo;
        }

        public String getLinkFacebook() {
            return linkFacebook;
        }

    }

    @NonNull
    @Override
    public String toString() {
        return "BroadcastModel{" +
                "_id='" + _id + '\'' +
                ", countReactions=" + countReactions +
                ", maxCountViews=" + maxCountViews +
                ", title='" + title + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", status='" + status + '\'' +
                ", listLive=" + listLive +
                ", linkVideo='" + linkVideo + '\'' +
                ", shopName='" + shopName + '\'' +
                ", avatarOwner='" + avatarOwner + '\'' +
                ", shopID='" + shopID + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
