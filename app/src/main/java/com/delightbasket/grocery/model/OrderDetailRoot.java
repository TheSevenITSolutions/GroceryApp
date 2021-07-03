package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailRoot {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class Data {

        @SerializedName("item_details")
        private List<ItemDetailsItem> itemDetails;

        @SerializedName("delivery_details")
        private List<DeliveryDetailsItem> deliveryDetails;

        @SerializedName("order_details")
        private List<OrderDetailsItem> orderDetails;

        public List<ItemDetailsItem> getItemDetails() {
            return itemDetails;
        }

        public List<DeliveryDetailsItem> getDeliveryDetails() {
            return deliveryDetails;
        }

        public List<OrderDetailsItem> getOrderDetails() {
            return orderDetails;
        }
    }

    public static class DeliveryDetailsItem {

        @SerializedName("home_no")
        private String homeNo;

        @SerializedName("area")
        private String area;

        @SerializedName("pincode")
        private String pincode;

        @SerializedName("address")
        private String address;

        @SerializedName("city")
        private String city;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("delivery_address_id")
        private String deliveryAddressId;

        @SerializedName("society")
        private String society;

        @SerializedName("street")
        private String street;

        @SerializedName("mobile_number")
        private String mobileNumber;

        @SerializedName("landmark")
        private String landmark;

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("status")
        private String status;

        public String getHomeNo() {
            return homeNo;
        }

        public String getArea() {
            return area;
        }

        public String getPincode() {
            return pincode;
        }

        public String getAddress() {
            return address;
        }

        public String getCity() {
            return city;
        }

        public String getLastName() {
            return lastName;
        }

        public String getDeliveryAddressId() {
            return deliveryAddressId;
        }

        public String getSociety() {
            return society;
        }

        public String getStreet() {
            return street;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public String getLandmark() {
            return landmark;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class ItemDetailsItem {

        @SerializedName("unit")
        private String unit;

        @SerializedName("quantity")
        private String quantity;

        @SerializedName("item_id")
        private String itemId;

        @SerializedName("product_image")
        private List<String> productImage;

        @SerializedName("price")
        private String price;

        @SerializedName("product_id")
        private String productId;

        @SerializedName("name")
        private String name;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("price_unit_id")
        private String priceUnitId;

        public String getUnit() {
            return unit;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getItemId() {
            return itemId;
        }

        public List<String> getProductImage() {
            return productImage;
        }

        public String getPrice() {
            return price;
        }

        public String getProductId() {
            return productId;
        }

        public String getName() {
            return name;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getPriceUnitId() {
            return priceUnitId;
        }
    }

    public static class OrderDetailsItem {

        @SerializedName("total_item")
        private int totalItem;

        @SerializedName("payment_type")
        private String paymentType;

        @SerializedName("shipping_charge")
        private String shippingCharge;

        @SerializedName("total_amount")
        private int totalAmount;

        @SerializedName("ordered_at")
        private String orderedAt;

        @SerializedName("review")
        private Review review;

        @SerializedName("sub_total")
        private int subTotal;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("coupon_discount")
        private String couponDiscount;

        public int getTotalItem() {
            return totalItem;
        }

        public String getPaymentType() {
            return paymentType;
        }

        public String getShippingCharge() {
            return shippingCharge;
        }

        public int getTotalAmount() {
            return totalAmount;
        }

        public String getOrderedAt() {
            return orderedAt;
        }

        public Review getReview() {
            return review;
        }

        public void setReview(Review review) {

            this.review = review;
        }

        public int getSubTotal() {
            return subTotal;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getCouponDiscount() {
            return couponDiscount;
        }
    }

    public class Review {
        private String review;
        private int rating = 0;

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }
}