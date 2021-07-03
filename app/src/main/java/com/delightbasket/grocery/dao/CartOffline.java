package com.delightbasket.grocery.dao;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CartOffline {

    @PrimaryKey(autoGenerate = true)
    int id;


    String pid;
    String name;
    String imageUrl;

    String priceUnitId;

    long quantity;

    String price;

    String unit;

    public CartOffline(String pid, String name, String imageUrl, String priceUnitId, long quantity, String price, String unit) {
        this.pid = pid;
        this.imageUrl = imageUrl;
        this.name = name;
        this.priceUnitId = priceUnitId;
        this.quantity = quantity;
        this.price = price;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPriceUnitId() {
        return priceUnitId;
    }

    public void setPriceUnitId(String priceUnitId) {
        this.priceUnitId = priceUnitId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
