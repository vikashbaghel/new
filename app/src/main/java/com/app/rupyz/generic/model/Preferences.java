package com.app.rupyz.generic.model;

import com.google.gson.annotations.SerializedName;

public class Preferences {
    
    @SerializedName("WHATSAPP_OTP_IN")
    public boolean isWHATSAPP_OTP_IN() {
        return WHATSAPP_OTP_IN;
    }

    public void setWHATSAPP_OTP_IN(boolean WHATSAPP_OTP_IN) {
        this.WHATSAPP_OTP_IN = WHATSAPP_OTP_IN;
    }

    private boolean WHATSAPP_OTP_IN;
}
