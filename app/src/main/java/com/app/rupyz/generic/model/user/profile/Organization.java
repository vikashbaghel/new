package com.app.rupyz.generic.model.user.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Organization {
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("selected_authorized_signatory")
    @Expose
    private String selectedAuthorizedSignatory;
    @SerializedName("score_value")
    @Expose
    private Integer scoreValue;
    @SerializedName("score_comment")
    @Expose
    private String scoreComment;
    @SerializedName("credit_age")
    @Expose
    private String creditAge;
    @SerializedName("days_remaining")
    @Expose
    private Integer daysRemaining;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getSelectedAuthorizedSignatory() {
        return selectedAuthorizedSignatory;
    }

    public void setSelectedAuthorizedSignatory(String selectedAuthorizedSignatory) {
        this.selectedAuthorizedSignatory = selectedAuthorizedSignatory;
    }

    public Integer getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Integer scoreValue) {
        this.scoreValue = scoreValue;
    }

    public String getScoreComment() {
        return scoreComment;
    }

    public void setScoreComment(String scoreComment) {
        this.scoreComment = scoreComment;
    }

    public String getCreditAge() {
        return creditAge;
    }

    public void setCreditAge(String creditAge) {
        this.creditAge = creditAge;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
