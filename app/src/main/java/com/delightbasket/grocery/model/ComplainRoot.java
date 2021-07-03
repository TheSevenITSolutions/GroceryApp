package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ComplainRoot {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public List<DataItem> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class DataItem {

        @SerializedName("complaint_id")
        private String complaintId;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("description")
        private String description;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("status")
        private String status;

        public String getComplaintId() {
            return complaintId;
        }

        public String getUserId() {
            return userId;
        }

        public String getDescription() {
            return description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getStatus() {
            return status;
        }
    }
}