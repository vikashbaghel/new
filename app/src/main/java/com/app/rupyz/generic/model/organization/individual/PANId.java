package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class PANId {
    private String seq;
    @SerializedName("IdNumber")
    private String idNumber;

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(String reportedDate) {
        this.reportedDate = reportedDate;
    }

    @SerializedName("ReportedDate")
    private String reportedDate;
}
