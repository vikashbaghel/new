package com.app.rupyz.generic.model.organization.selectgst;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GstData {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("primary_gstin")
    @Expose
    private String primaryGstin;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrimaryGstin() {
        return primaryGstin;
    }

    public void setPrimaryGstin(String primaryGstin) {
        this.primaryGstin = primaryGstin;
    }

}
