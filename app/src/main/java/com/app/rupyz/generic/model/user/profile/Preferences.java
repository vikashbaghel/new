package com.app.rupyz.generic.model.user.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Preferences {
    @SerializedName("WHATSAPP_OTP_IN")
    @Expose
    private Boolean whatsappOtpIn;

    public Boolean getWhatsappOtpIn() {
        return whatsappOtpIn;
    }

    public void setWhatsappOtpIn(Boolean whatsappOtpIn) {
        this.whatsappOtpIn = whatsappOtpIn;
    }
}
