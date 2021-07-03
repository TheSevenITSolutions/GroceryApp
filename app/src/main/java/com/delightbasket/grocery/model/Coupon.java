
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class Coupon {

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

        @SerializedName("coupon_code")
        private String couponCode;
        @SerializedName("coupon_discount")
        private String couponDiscount;
        @SerializedName("discount_type")
        private Long discountType;
        @Expose
        private Long id;
        @SerializedName("minimum_amount")
        private String minimumAmount;

        public String getCouponCode() {
            return couponCode;
        }

        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
        }

        public String getCouponDiscount() {
            return couponDiscount;
        }

        public void setCouponDiscount(String couponDiscount) {
            this.couponDiscount = couponDiscount;
        }

        public Long getDiscountType() {
            return discountType;
        }

        public void setDiscountType(Long discountType) {
            this.discountType = discountType;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMinimumAmount() {
            return minimumAmount;
        }

        public void setMinimumAmount(String minimumAmount) {
            this.minimumAmount = minimumAmount;
        }

    }
}
