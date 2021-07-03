
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Wishlist {

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

        @Expose
        private String name;
        @SerializedName("price_unit")
        private List<PriceUnit> priceUnit;
        @SerializedName("product_id")
        private String productId;
        @SerializedName("product_image")
        private List<String> productImage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<PriceUnit> getPriceUnit() {
            return priceUnit;
        }

        public void setPriceUnit(List<PriceUnit> priceUnit) {
            this.priceUnit = priceUnit;
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

    }

        @SuppressWarnings("unused")
    public static class PriceUnit {

        @SerializedName("is_cart")
        private Long isCart;
        @Expose
        private String price;
            @SerializedName("price_unit_id")
            private String priceUnitId;
            @Expose
            private Long quantity;
            @Expose
            private String unit;

        public Long getIsCart() {
            return isCart;
        }

        public void setIsCart(Long isCart) {
            this.isCart = isCart;
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

            public Long getQuantity() {
                return quantity;
            }

            public void setQuantity(Long quantity) {
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
