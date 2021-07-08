package com.delightbasket.grocery.model.subscriptionmodel;

public class PriceUnitItem {
    private int isCart;
    private String unit;
    private int quantity;
    private String price;
    private int discountPrice;
    private String priceUnitId;

    public int getIsCart() {
        return isCart;
    }

    public void setIsCart(int isCart) {
        this.isCart = isCart;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getPriceUnitId() {
        return priceUnitId;
    }

    public void setPriceUnitId(String priceUnitId) {
        this.priceUnitId = priceUnitId;
    }

    @Override
    public String toString() {
        return
                "PriceUnitItem{" +
                        "is_cart = '" + isCart + '\'' +
                        ",unit = '" + unit + '\'' +
                        ",quantity = '" + quantity + '\'' +
                        ",price = '" + price + '\'' +
                        ",discount_price = '" + discountPrice + '\'' +
                        ",price_unit_id = '" + priceUnitId + '\'' +
                        "}";
    }
}
