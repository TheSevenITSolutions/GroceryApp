
package com.delightbasket.grocery.model;

import com.google.gson.annotations.Expose;

@SuppressWarnings("unused")
public class RestResponse {

    @Expose
    private String message;
    @Expose
    private Long status;

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

}
