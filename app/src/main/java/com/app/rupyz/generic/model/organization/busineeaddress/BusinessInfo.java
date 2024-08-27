package com.app.rupyz.generic.model.organization.busineeaddress;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessInfo {
    @SerializedName("data")
    @Expose
    private BusinessData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public BusinessData getData() {
        return data;
    }

    public void setData(BusinessData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
