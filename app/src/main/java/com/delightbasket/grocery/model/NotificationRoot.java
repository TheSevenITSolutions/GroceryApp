package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationRoot {

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

        @SerializedName("notification_type")
        private int notificationType;

        @SerializedName("image")
        private String image;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("item_id")
        private String itemId;

        @SerializedName("title")
        private String title;

        @SerializedName("message")
        private String message;

        public int getNotificationType() {
            return notificationType;
        }

        public String getImage() {
            return image;
        }

        public String getUserId() {
            return userId;
        }

        public String getItemId() {
            return itemId;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }
    }
}