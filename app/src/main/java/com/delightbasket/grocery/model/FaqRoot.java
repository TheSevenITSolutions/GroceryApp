package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FaqRoot {

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

        @SerializedName("question")
        private String question;

        @SerializedName("answer")
        private String answer;

        @SerializedName("id")
        private int id;

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public int getId() {
            return id;
        }
    }
}