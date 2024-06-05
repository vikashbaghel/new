package com.app.rupyz.generic.model.organization.gstinfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GSTData {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gstin_list")
    @Expose
    private List<GstinList> gstinList = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<GstinList> getGstinList() {
        return gstinList;
    }

    public void setGstinList(List<GstinList> gstinList) {
        this.gstinList = gstinList;
    }
}
