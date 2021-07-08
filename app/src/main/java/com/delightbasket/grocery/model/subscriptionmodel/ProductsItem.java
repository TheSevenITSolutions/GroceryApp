package com.delightbasket.grocery.model.subscriptionmodel;

import java.util.List;

public class ProductsItem {
    private List<PriceUnitItem> priceUnit;
    private String categoryName;
    private String stockStatus;
    private String categoryId;
    private List<String> productImage;
    private String productId;
    private String description;
    private int id;
    private String productName;
    private int isWishlist;

    public List<PriceUnitItem> getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(List<PriceUnitItem> priceUnit) {
        this.priceUnit = priceUnit;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<String> productImage) {
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getIsWishlist() {
        return isWishlist;
    }

    public void setIsWishlist(int isWishlist) {
        this.isWishlist = isWishlist;
    }

    @Override
    public String toString() {
        return
                "ProductsItem{" +
                        "price_unit = '" + priceUnit + '\'' +
                        ",category_name = '" + categoryName + '\'' +
                        ",stock_status = '" + stockStatus + '\'' +
                        ",category_id = '" + categoryId + '\'' +
                        ",product_image = '" + productImage + '\'' +
                        ",product_id = '" + productId + '\'' +
                        ",description = '" + description + '\'' +
                        ",id = '" + id + '\'' +
                        ",product_name = '" + productName + '\'' +
                        ",is_wishlist = '" + isWishlist + '\'' +
                        "}";
    }
}