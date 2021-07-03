
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class OrderRoot {

    @Expose
    private List<Datum> data;
    @Expose
    private String message;
    @Expose
    private Long status;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @SuppressWarnings("unused")
    public static class Datum {

        @SerializedName("order_id")
        private String orderId;
        @SerializedName("ordered_at")
        private String orderedAt;
        @Expose
        private String status;
        @SerializedName("total_amount")
        private Long totalAmount;
        @SerializedName("total_item")
        private Long totalItem;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getOrderedAt() {
            return orderedAt;
        }

        public void setOrderedAt(String orderedAt) {
            this.orderedAt = orderedAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Long getTotalItem() {
            return totalItem;
        }

        public void setTotalItem(Long totalItem) {
            this.totalItem = totalItem;
        }

    }
}
