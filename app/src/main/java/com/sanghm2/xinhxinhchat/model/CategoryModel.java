package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryModel {
    private final String _id;
    private final String name;
    private final String imageUrl;
    private boolean isChecked;

    public CategoryModel(String _id, String name, String imageUrl, boolean isChecked) {
        this._id = _id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.isChecked = isChecked;
    }

    public static List<CategoryModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Gson gson = new Gson();
            return gson.fromJson(jsonObject.getString("data"), new TypeToken<List<CategoryModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "CategoryModel{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
