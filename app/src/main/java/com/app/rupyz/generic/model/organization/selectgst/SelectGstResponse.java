package com.app.rupyz.generic.model.organization.selectgst;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SelectGstResponse {
    @SerializedName("data")
    @Expose
    private GstData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public GstData getData() {
        return data;
    }

    public void setData(GstData data) {
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
