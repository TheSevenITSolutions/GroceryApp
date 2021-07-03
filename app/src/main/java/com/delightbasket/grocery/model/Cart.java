
package com.delightbasket.grocery.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Cart {

    @Expose
    private List<Datum> data;
    @Expose
    private String message;
    @Expose
    private Long status;
    @SerializedName("total_amount")
    private Long totalAmount;

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

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

        @SuppressWarnings("unused")
    public static class Datum {

        @Expose
        private String name;
        @Expose
        private String price;
        @SerializedName("price_unit_id")
        private String priceUnitId;
        @SerializedName("product_id")
        private String productId;
        @SerializedName("product_image")
        private List<String> productImage;
        @Expose
        private String quantity;
        @Expose
        private String unit;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPriceUnitId() {
            return priceUnitId;
        }

        public void setPriceUnitId(String priceUnitId) {
            this.priceUnitId = priceUnitId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public List<String> getProductImage() {
            return productImage;
        }

        public void setProductImage(List<String> productImage) {
            this.productImage = productImage;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

    }
}
