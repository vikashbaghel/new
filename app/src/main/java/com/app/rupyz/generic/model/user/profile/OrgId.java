package com.app.rupyz.generic.model.user.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrgId {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("pan_id")
    @Expose
    private String panId;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("reg_step")
    @Expose
    private Integer regStep;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("equifax_score")
    @Expose
    private Integer equifaxScore;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public Integer getRegStep() {
        return regStep;
    }

    public void setRegStep(Integer regStep) {
        this.regStep = regStep;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getEquifaxScore() {
        return equifaxScore;
    }

    public void setEquifaxScore(Integer equifaxScore) {
        this.equifaxScore = equifaxScore;
    }
}
