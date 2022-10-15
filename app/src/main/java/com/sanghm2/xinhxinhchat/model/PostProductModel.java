package com.sanghm2.xinhxinhchat.model;

public class PostProductModel {
    private int sellPrice;
    private String status;
    private Boolean isOrder;
    private String name;
    private String description;
    private String sku;
    private String unitID;
    private String categoryID;

    public PostProductModel(int sellPrice, String status, Boolean isOrder, String name, String description, String sku, String unitID, String categoryID) {
        this.sellPrice = sellPrice;
        this.status = status;
        this.isOrder = isOrder;
        this.name = name;
        this.description = description;
        this.sku = sku;
        this.unitID = unitID;
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return "PostProductModel{" +
                "sellPrice=" + sellPrice +
                ", status='" + status + '\'' +
                ", isOrder=" + isOrder +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", sku='" + sku + '\'' +
                ", unitID='" + unitID + '\'' +
                ", categoryID='" + categoryID + '\'' +
                '}';
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
}
