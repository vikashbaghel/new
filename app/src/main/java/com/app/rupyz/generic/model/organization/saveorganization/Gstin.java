package com.app.rupyz.generic.model.organization.saveorganization;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Gstin {

    @SerializedName("gstin")
    @Expose
    private String gstin;
    @SerializedName("state")
    @Expose
    private String state;

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
