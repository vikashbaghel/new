package com.app.rupyz.generic.model.report_download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DownloadResponse {
    @SerializedName("is_generated")
    @Expose
    private Boolean isGenerated;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("report_file_info")
    @Expose
    private ReportFileInfo reportFileInfo;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;

    public Boolean getIsGenerated() {
        return isGenerated;
    }

    public void setIsGenerated(Boolean isGenerated) {
        this.isGenerated = isGenerated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ReportFileInfo getReportFileInfo() {
        return reportFileInfo;
    }

    public void setReportFileInfo(ReportFileInfo reportFileInfo) {
        this.reportFileInfo = reportFileInfo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }


}
