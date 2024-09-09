package com.app.rupyz.generic.model.forcedUpdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateInfoModel {

    @SerializedName("version_no")
    @Expose
    private Integer versionNo;
    @SerializedName("version_name")
    @Expose
    private String versionName;
    @SerializedName("is_forced_update")
    @Expose
    private Boolean isForcedUpdate;
    @SerializedName("is_live")
    @Expose
    private Boolean isLive;
    @SerializedName("device_type")
    @Expose
    private String deviceType;

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Boolean getIsForcedUpdate() {
        return isForcedUpdate;
    }

    public void setIsForcedUpdate(Boolean isForcedUpdate) {
        this.isForcedUpdate = isForcedUpdate;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
