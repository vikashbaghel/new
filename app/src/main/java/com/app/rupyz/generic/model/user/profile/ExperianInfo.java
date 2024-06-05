package com.app.rupyz.generic.model.user.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExperianInfo {
    @SerializedName("credit_age")
    @Expose
    private String creditAge;
    @SerializedName("score_comment")
    @Expose
    private String scoreComment;
    @SerializedName("report_pause_days")
    @Expose
    private Integer reportPauseDays;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("score_value")
    @Expose
    private Integer scoreValue;

    public String getCreditAge() {
        return creditAge;
    }

    public void setCreditAge(String creditAge) {
        this.creditAge = creditAge;
    }

    public String getScoreComment() {
        return scoreComment;
    }

    public void setScoreComment(String scoreComment) {
        this.scoreComment = scoreComment;
    }

    public Integer getReportPauseDays() {
        return reportPauseDays;
    }

    public void setReportPauseDays(Integer reportPauseDays) {
        this.reportPauseDays = reportPauseDays;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Integer scoreValue) {
        this.scoreValue = scoreValue;
    }
}
