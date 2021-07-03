
package com.delightbasket.grocery.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class CategoryProduct {

    @Expose
    private Data data;
    @Expose
    private String message;
    @Expose
    private Long status;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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
    public static class Data {

        @SerializedName("category_id")
        private String categoryId;
        @SerializedName("category_name")
        private String categoryName;
        @Expose
        private List<Product> products;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

    }

    @SuppressWarnings("unused")
    public static class PriceUnit {

        @SerializedName("discount_price")
        private Long discountPrice;
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

        public Long getDiscountPrice() {
            return discountPrice;
        }

        public void setDiscountPrice(Long discountPrice) {
            this.discountPrice = discountPrice;
        }

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

    @SuppressWarnings("unused")
    public static class Product {

        @SerializedName("category_id")
        private String categoryId;
        @SerializedName("category_name")
        private String categoryName;
        @Expose
        private String description;
        @Expose
        private Long id;
        @SerializedName("is_wishlist")
        private Long isWishlist;
        @SerializedName("price_unit")
        private List<PriceUnit> priceUnit;
        @SerializedName("product_id")
        private String productId;
        @SerializedName("product_image")
        private List<String> productImage;
        @SerializedName("product_name")
        private String productName;
        @SerializedName("stock_status")
        private String stockQuantity;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getIsWishlist() {
            return isWishlist;
        }

        public void setIsWishlist(Long isWishlist) {
            this.isWishlist = isWishlist;
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

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(String stockQuantity) {
            this.stockQuantity = stockQuantity;
        }

    }
}
