package com.app.rupyz.generic.model.forcedUpdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateResponse {


    @SerializedName("data")
    @Expose
    private List<UpdateInfoModel> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private Boolean error;

    public List<UpdateInfoModel> getData() {
        return data;
    }

    public void setData(List<UpdateInfoModel> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

}

