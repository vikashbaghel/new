package com.app.rupyz.generic.model;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class LoginModelForAccess {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccess_type() {
        return access_type;
    }

    public void setAccess_type(String access_type) {
        this.access_type = access_type;
    }

    @SerializedName("username")
    private String username;
    @SerializedName("access_type")
    private String access_type;
    @SerializedName("otp_ref")
    private String otp_ref;
    @SerializedName("otp")
    private String otp;
    @SerializedName("terms_condition")
    private boolean terms_condition;
    @SerializedName("is_smart_match")
    private boolean is_smart_match;

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    @SerializedName("preferences")
    private Preferences preferences;


    public LoginModelForAccess(String username, String access_type, String otp, String otp_ref, boolean terms_condition,
                               boolean whatsapp, boolean is_smart_match) {
        this.username = username;
        this.access_type = access_type;
        this.otp = otp;
        this.otp_ref = otp_ref;
        this.terms_condition = terms_condition;
        this.is_smart_match = is_smart_match;
        Preferences preferences = new Preferences();
        preferences.setWHATSAPP_OTP_IN(whatsapp);
        this.preferences = preferences;

    }
}
