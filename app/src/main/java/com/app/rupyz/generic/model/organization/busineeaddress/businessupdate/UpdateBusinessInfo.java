package com.app.rupyz.generic.model.organization.busineeaddress.businessupdate;

import com.app.rupyz.generic.model.organization.busineeaddress.BusinessData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateBusinessInfo {
    @SerializedName("data")
    @Expose
    private UpdateBusinessData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public UpdateBusinessData getData() {
        return data;
    }

    public void setData(UpdateBusinessData data) {
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
