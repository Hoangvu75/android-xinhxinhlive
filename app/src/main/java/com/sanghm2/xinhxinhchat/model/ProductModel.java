package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductModel {
    private String _id;
    private List<String> imageUrls;
    private int sellPrice;
    private String status;
    private Boolean isOrder;
    private String name;
    private String description;
    private String sku;
    private String unitID;
    private String categoryID;

    public ProductModel(String _id, List<String> imageUrls, int sellPrice, String status, Boolean isOrder, String name, String description, String sku, String unitID, String categoryID) {
        this._id = _id;
        this.imageUrls = imageUrls;
        this.sellPrice = sellPrice;
        this.status = status;
        this.isOrder = isOrder;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.unitID = unitID;
        this.categoryID = categoryID;
    }

    public static List<ProductModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            Gson gson = new Gson();
            return gson.fromJson(jsonData.getString("data"), new TypeToken<List<ProductModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static ProductModel FromJson2(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Gson gson = new Gson();
            return gson.fromJson(jsonObject.getString("data"), new TypeToken<ProductModel>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return null;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getOrder() {
        return isOrder;
    }

    public void setOrder(Boolean order) {
        isOrder = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getUnitID() {
        return unitID;
    }

    public void setUnitID(String unitID) {
        this.unitID = unitID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
