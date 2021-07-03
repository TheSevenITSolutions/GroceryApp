
package com.delightbasket.grocery.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class RestResponse2 {

    @SerializedName("response_code")
    private Long responseCode;
    @SerializedName("response_message")
    private String responseMessage;
    @SerializedName("success_code")
    private Long successCode;

    public Long getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Long responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Long getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(Long successCode) {
        this.successCode = successCode;
    }

}
