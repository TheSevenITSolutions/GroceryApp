package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainCategory {


    @Expose
    @SerializedName("data")
    public Data data;
    @Expose
    @SerializedName("message")
    public String message;
    @Expose
    @SerializedName("status")
    public int status;

    public static class Data {
        @Expose
        @SerializedName("total")
        public int total;
        @Expose
        @SerializedName("to")
        public int to;
        @Expose
        @SerializedName("prev_page_url")
        public String prev_page_url;
        @Expose
        @SerializedName("per_page")
        public int per_page;
        @Expose
        @SerializedName("path")
        public String path;
        @Expose
        @SerializedName("next_page_url")
        public String next_page_url;
        @Expose
        @SerializedName("last_page_url")
        public String last_page_url;
        @Expose
        @SerializedName("last_page")
        public int last_page;
        @Expose
        @SerializedName("from")
        public int from;
        @Expose
        @SerializedName("first_page_url")
        public String first_page_url;
        @Expose
        @SerializedName("data")
        public List<Data1> data;
        @Expose
        @SerializedName("current_page")
        public int current_page;
    }

    public static class Data1 {
        @Expose
        @SerializedName("category_image")
        public String category_image;
        @Expose
        @SerializedName("category_name")
        public String category_name;
        @Expose
        @SerializedName("id")
        public int id;
    }
}
