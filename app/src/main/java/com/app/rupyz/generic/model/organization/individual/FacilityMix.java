package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class FacilityMix {
    @SerializedName("SECURED LOAN")
    private int sECUREDLOAN;

    public int getsECUREDLOAN() {
        return sECUREDLOAN;
    }

    public void setsECUREDLOAN(int sECUREDLOAN) {
        this.sECUREDLOAN = sECUREDLOAN;
    }

    public int getuNSECUREDLOAN() {
        return uNSECUREDLOAN;
    }

    public void setuNSECUREDLOAN(int uNSECUREDLOAN) {
        this.uNSECUREDLOAN = uNSECUREDLOAN;
    }

    @SerializedName("UNSECURED LOAN")
    private int uNSECUREDLOAN;
}
