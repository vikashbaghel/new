package com.app.rupyz.generic.model.organization.saveorganization;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveOrganizationRequest {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("email")
    @Expose
    private String email;

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    @SerializedName("pan_id")
    @Expose
    private String panId;

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

}
