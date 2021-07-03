package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

public class ShippingChargeRoot {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class Data {

        @SerializedName("shipping_charge")
        private int shippingCharge;

        public int getShippingCharge() {
            return shippingCharge;
        }
    }
}