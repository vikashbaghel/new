package com.app.rupyz.generic.model.organization.createorganization;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOrganizationRequest {

    @SerializedName("pan_id")
    @Expose
    private String panId;

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }
}
