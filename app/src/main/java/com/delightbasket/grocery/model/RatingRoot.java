package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RatingRoot {

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

        @SerializedName("user_id")
        private String userId;

        @SerializedName("review")
        private String review;

        @SerializedName("rating")
        private int rating;

        @SerializedName("id")
        private int id;

        @SerializedName("order_id")
        private String orderId;

        public String getUserId() {
            return userId;
        }

        public String getReview() {
            return review;
        }

        public int getRating() {
            return rating;
        }

        public int getId() {
            return id;
        }

        public String getOrderId() {
            return orderId;
        }
    }
}