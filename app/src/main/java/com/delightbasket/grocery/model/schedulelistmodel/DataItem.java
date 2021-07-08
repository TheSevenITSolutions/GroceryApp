package com.delightbasket.grocery.model.schedulelistmodel;

import com.google.gson.annotations.SerializedName;

public class DataItem {

    @SerializedName("id")
    private int id;

    @SerializedName("schedule_name")
    private String scheduleName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    @Override
    public String toString() {
        return
                "DataItem{" +
                        "id = '" + id + '\'' +
                        ",schedule_name = '" + scheduleName + '\'' +
                        "}";
    }
}