package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

public class WalletDataResponse {
    @SerializedName("data")
    private WalletDataResponseData data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public WalletDataResponseData getData() {
        return data;
    }

    public void setData(WalletDataResponseData data) {
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

    public static class WalletDataResponseData {

        @SerializedName("total_amount")
        private String total_amount;

        @SerializedName("user_id")
        private String user_id;

        @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return user_id;
        }

        public void setUserId(String user_id) {
            this.user_id = user_id;
        }

        public String getAmount() {
            return total_amount;
        }

        public void setAmount(String total_amount) {
            this.total_amount = total_amount;
        }
    }
}