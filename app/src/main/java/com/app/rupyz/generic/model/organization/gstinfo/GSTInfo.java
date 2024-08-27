package com.app.rupyz.generic.model.organization.gstinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GSTInfo {
    @SerializedName("data")
    @Expose
    private GSTData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public GSTData getData() {
        return data;
    }

    public void setData(GSTData data) {
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
