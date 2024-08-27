package com.app.rupyz.generic.model.notification.fcm;

import com.app.rupyz.generic.model.Preferences;
import com.google.gson.annotations.SerializedName;

public class FcmInfoModel {


    @SerializedName("device_type")
    private String device_type;

    @SerializedName("device_manufacture")
    private String device_manufacture;

    @SerializedName("os_type")
    private String os_type;

    @SerializedName("device_model")
    private String device_model;

    @SerializedName("fcm_token")
    private String fcm_token;


    public FcmInfoModel(String device_type, String device_manufacture, String os_type,
                        String device_model, String fcm_token
    ) {
        this.device_type = device_type;
        this.device_manufacture = device_manufacture;
        this.os_type = os_type;
        this.device_model = device_model;
        this.fcm_token = fcm_token;
    }
    
}
