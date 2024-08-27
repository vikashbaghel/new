package com.app.rupyz.generic.model.profile.testimonial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestimonialInfoModel {
    @SerializedName("data")
    @Expose
    private List<TestimonialData> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public List<TestimonialData> getData() {
        return data;
    }

    public void setData(List<TestimonialData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
