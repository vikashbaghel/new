package com.app.rupyz.generic.model.organization;

public class EquiFaxOtpInfoModel {

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }


    private Metadata metadata;

    public String getOtp_ref() {
        return otp_ref;
    }

    public void setOtp_ref(String otp_ref) {
        this.otp_ref = otp_ref;
    }

    private String otp_ref;


}