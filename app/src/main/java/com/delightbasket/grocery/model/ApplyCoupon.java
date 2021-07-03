
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ApplyCoupon {

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

        @SerializedName("coupon_discount")
        private Long couponDiscount;
        @SerializedName("discount_percent")
        private String discountPercent;
        @Expose
        private Long subtotal;

        public Long getCouponDiscount() {
            return couponDiscount;
        }

        public void setCouponDiscount(Long couponDiscount) {
            this.couponDiscount = couponDiscount;
        }

        public String getDiscountPercent() {
            return discountPercent;
        }

        public void setDiscountPercent(String discountPercent) {
            this.discountPercent = discountPercent;
        }

        public Long getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(Long subtotal) {
            this.subtotal = subtotal;
        }

    }
}
