package com.app.rupyz.generic.model.profile.profileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrgProfileInfoModel {

    @SerializedName("data")
    @Expose
    private OrgProfileDetail data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private Boolean error;

    public OrgProfileDetail getData() {
        return data;
    }

    public void setData(OrgProfileDetail data) {
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
