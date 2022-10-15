package com.sanghm2.xinhxinhchat.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductInLivestreamModel {
    private int sellPrice;
    private ProductInfo productInfo;

    public static List<ProductInLivestreamModel> FromJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Gson gson = new Gson();
            return gson.fromJson(jsonObject.getString("data"), new TypeToken<List<ProductInLivestreamModel>>() {
            }.getType());
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public ProductInfo getProductInfo() {
        return productInfo;
    }

    public class ProductInfo {
        private List<String> imageUrls;
        private String name;
        private String sku;
        private int sellPrice;

        public List<String> getImageUrls() {
            return imageUrls;
        }

        public String getName() {
            return name;
        }

        public String getSku() {
            return sku;
        }

        public int getSellPrice() {
            return sellPrice;
        }
    }
}
