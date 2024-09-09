package com.app.rupyz.generic.model.individual.experian;

import com.google.gson.annotations.SerializedName;

public class FacilityMix {

    @SerializedName("SECURED LOAN")
    public int securedLoan;

    public int getSecuredLoan() {
        return securedLoan;
    }

    public void setSecuredLoan(int securedLoan) {
        this.securedLoan = securedLoan;
    }

    public int getUnsecuredLoan() {
        return unsecuredLoan;
    }

    public void setUnsecuredLoan(int unsecuredLoan) {
        this.unsecuredLoan = unsecuredLoan;
    }

    @SerializedName("UNSECURED LOAN")
    public int unsecuredLoan;
}
