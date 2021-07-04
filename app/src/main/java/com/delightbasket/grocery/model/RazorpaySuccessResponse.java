package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

public class RazorpaySuccessResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

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
}
