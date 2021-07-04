package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

public class RazorpayOrderIdResponse {
    @SerializedName("data")
    private RazorpayOrderIdResponseData data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public RazorpayOrderIdResponseData getData() {
        return data;
    }

    public void setData(RazorpayOrderIdResponseData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class RazorpayOrderIdResponseData {

        @SerializedName("amount")
        private String amount;

        @SerializedName("order_id")
        private String order_id;

        @SerializedName("user_id")
        private String user_id;

        public String getUserId() {
            return user_id;
        }

        public void setUserId(String user_id) {
            this.user_id = user_id;
        }

        public String getOrderId() {
            return order_id;
        }

        public void setOrderId(String order_id) {
            this.order_id = order_id;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}

