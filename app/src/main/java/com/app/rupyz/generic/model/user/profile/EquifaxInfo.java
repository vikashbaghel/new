package com.app.rupyz.generic.model.user.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EquifaxInfo {
    @SerializedName("org_id")
    @Expose
    private Integer orgId;
    @SerializedName("equifax_score")
    @Expose
    private Integer equifaxScore;
    @SerializedName("organization")
    @Expose
    private Organization organization;
    @SerializedName("user")
    @Expose
    private User user;

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public Integer getEquifaxScore() {
        return equifaxScore;
    }

    public void setEquifaxScore(Integer equifaxScore) {
        this.equifaxScore = equifaxScore;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
