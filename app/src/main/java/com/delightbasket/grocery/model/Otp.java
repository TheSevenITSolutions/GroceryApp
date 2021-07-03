
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Otp {

    @Expose
    @SerializedName("data")
    private Data data;
    @Expose
    @SerializedName("success_code")
    private int success_code;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getSuccess_code() {
        return success_code;
    }

    public void setSuccess_code(int success_code) {
        this.success_code = success_code;
    }

    public static class Data {
        @Expose
        @SerializedName("Details")
        private String Details;
        @Expose
        @SerializedName("Status")
        private String Status;

        public String getDetails() {
            return Details;
        }

        public void setDetails(String Details) {
            this.Details = Details;
        }

        public String getStatus() {
            return Status;
        }

        public void setStatus(String Status) {
            this.Status = Status;
        }
    }
}
