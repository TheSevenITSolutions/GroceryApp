package com.delightbasket.grocery.model.postsubscription;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("subscription_id")
    private String subscriptionId;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("updated_at")
    private Object updatedAt;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("promo_code")
    private String promoCode;

    @SerializedName("id")
    private int id;

    @SerializedName("delivery_address_id")
    private String deliveryAddressId;

    @SerializedName("schedule_id")
    private String scheduleId;

    @SerializedName("price_unit_id")
    private String priceUnitId;

    @SerializedName("status")
    private int status;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getPriceUnitId() {
        return priceUnitId;
    }

    public void setPriceUnitId(String priceUnitId) {
        this.priceUnitId = priceUnitId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return
                "Data{" +
                        "subscription_id = '" + subscriptionId + '\'' +
                        ",quantity = '" + quantity + '\'' +
                        ",updated_at = '" + updatedAt + '\'' +
                        ",user_id = '" + userId + '\'' +
                        ",product_id = '" + productId + '\'' +
                        ",created_at = '" + createdAt + '\'' +
                        ",promo_code = '" + promoCode + '\'' +
                        ",id = '" + id + '\'' +
                        ",delivery_address_id = '" + deliveryAddressId + '\'' +
                        ",schedule_id = '" + scheduleId + '\'' +
                        ",price_unit_id = '" + priceUnitId + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}