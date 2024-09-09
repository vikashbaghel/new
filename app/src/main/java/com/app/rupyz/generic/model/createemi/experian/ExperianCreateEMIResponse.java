package com.app.rupyz.generic.model.createemi.experian;

import com.app.rupyz.generic.model.createemi.EMIResponse;
import com.google.gson.annotations.SerializedName;

public class ExperianCreateEMIResponse {
    @SerializedName("data")
    private Datum createEMI;
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private Boolean error;

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

    public Datum getCreateEMI() {
        return createEMI;
    }

    public void setCreateEMI(Datum createEMI) {
        this.createEMI = createEMI;
    }
}
