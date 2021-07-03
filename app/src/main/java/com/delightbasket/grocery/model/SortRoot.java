package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SortRoot {

    @SerializedName("cartdata")
    private Cartdata cartdata;

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public Cartdata getCartdata() {
        return cartdata;
    }

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

        @SerializedName("price_unit")
        private List<PriceUnitItem> priceUnit;

        @SerializedName("category_name")
        private String categoryName;

        @SerializedName("stock_status")
        private String stockStatus;

        @SerializedName("coupon_code")
        private Object couponCode;

        @SerializedName("category_id")
        private String categoryId;

        @SerializedName("product_image")
        private List<String> productImage;

        @SerializedName("product_id")
        private String productId;

        @SerializedName("description")
        private String description;

        @SerializedName("discount")
        private Object discount;

        @SerializedName("id")
        private int id;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("is_wishlist")
        private int isWishlist;

        public List<PriceUnitItem> getPriceUnit() {
            return priceUnit;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getStockStatus() {
            return stockStatus;
        }

        public Object getCouponCode() {
            return couponCode;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public List<String> getProductImage() {
            return productImage;
        }

        public String getProductId() {
            return productId;
        }

        public String getDescription() {
            return description;
        }

        public Object getDiscount() {
            return discount;
        }

        public int getId() {
            return id;
        }

        public String getProductName() {
            return productName;
        }

        public int getIsWishlist() {
            return isWishlist;
        }
    }

    public static class Cartdata {

        @SerializedName("total_amount")
        private int totalAmount;

        @SerializedName("count")
        private int count;

        public int getTotalAmount() {
            return totalAmount;
        }

        public int getCount() {
            return count;
        }
    }

    public static class PriceUnitItem {

        @SerializedName("is_cart")
        private int isCart;

        @SerializedName("unit")
        private String unit;

        @SerializedName("quantity")
        private int quantity;

        @SerializedName("price")
        private String price;

        @SerializedName("discount_price")
        private float discountPrice;

        @SerializedName("price_unit_id")
        private String priceUnitId;

        public int getIsCart() {
            return isCart;
        }

        public String getUnit() {
            return unit;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getPrice() {
            return price;
        }

        public float getDiscountPrice() {
            return discountPrice;
        }

        public String getPriceUnitId() {
            return priceUnitId;
        }
    }
}