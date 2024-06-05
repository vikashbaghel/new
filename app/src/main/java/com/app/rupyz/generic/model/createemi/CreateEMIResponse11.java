package com.app.rupyz.generic.model.createemi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateEMIResponse11 {
    @SerializedName("data")
    private EMIResponse createEMI;
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

    public EMIResponse getCreateEMI() {
        return createEMI;
    }

    public void setCreateEMI(EMIResponse createEMI) {
        this.createEMI = createEMI;
    }
}
