package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PersonalInfo {
    @SerializedName("PANId")
    private List<PANId> pANId;

    public List<PANId> getpANId() {
        return pANId;
    }

    public void setpANId(List<PANId> pANId) {
        this.pANId = pANId;
    }

    public List<Passport> getPassport() {
        return passport;
    }

    public void setPassport(List<Passport> passport) {
        this.passport = passport;
    }

    @SerializedName("Passport")
    private List<Passport> passport;
}
