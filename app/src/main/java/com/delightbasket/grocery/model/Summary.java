
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Summary {

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
    public static class UserAddress {

        @Expose
        private String area;
        @Expose
        private String city;
        @SerializedName("delivery_address_id")
        private String deliveryAddressId;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("home_no")
        private String homeNo;
        @Expose
        private String landmark;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("mobile_number")
        private String mobileNumber;
        @Expose
        private String pincode;
        @Expose
        private String society;
        @Expose
        private String street;
        @SerializedName("user_id")
        private String userId;

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDeliveryAddressId() {
            return deliveryAddressId;
        }

        public void setDeliveryAddressId(String deliveryAddressId) {
            this.deliveryAddressId = deliveryAddressId;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getHomeNo() {
            return homeNo;
        }

        public void setHomeNo(String homeNo) {
            this.homeNo = homeNo;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getSociety() {
            return society;
        }

        public void setSociety(String society) {
            this.society = society;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }

    @SuppressWarnings("unused")
    public static class Item {

        @Expose
        private String name;
        @Expose
        private String price;
        @SerializedName("price_unit_id")
        private String priceUnitId;
        @SerializedName("product_id")
        private String productId;
        @Expose
        private String quantity;
        @Expose
        private Long total;
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

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

    }

    @SuppressWarnings("unused")
    public static class Data {

        @Expose
        private List<Item> items;
        @SerializedName("shipping_charge")
        private Long shippingCharge;
        @Expose
        private Long subtotal;
        @SerializedName("user_address")
        private List<UserAddress> userAddress;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public Long getShippingCharge() {
            return shippingCharge;
        }

        public void setShippingCharge(Long shippingCharge) {
            this.shippingCharge = shippingCharge;
        }

        public Long getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Long subtotal) {
            this.subtotal = subtotal;
        }

        public List<UserAddress> getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(List<UserAddress> userAddress) {
            this.userAddress = userAddress;
        }

    }
}
