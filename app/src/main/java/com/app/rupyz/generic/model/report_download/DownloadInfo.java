package com.app.rupyz.generic.model.report_download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
public class DownloadInfo {
    @SerializedName("data")
    @Expose
    private DownloadResponse data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private Boolean error;

    public DownloadResponse getData() {
        return data;
    }

    public void setData(DownloadResponse data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }


}