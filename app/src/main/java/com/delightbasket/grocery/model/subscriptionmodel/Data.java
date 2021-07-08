package com.delightbasket.grocery.model.subscriptionmodel;

import java.util.List;

public class Data {
    private String categoryName;
    private String categoryId;
    private List<ProductsItem> products;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public List<ProductsItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsItem> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return
                "Data{" +
                        "category_name = '" + categoryName + '\'' +
                        ",category_id = '" + categoryId + '\'' +
                        ",products = '" + products + '\'' +
                        "}";
    }
}