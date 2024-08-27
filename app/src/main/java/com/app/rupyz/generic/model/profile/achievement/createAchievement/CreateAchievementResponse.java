package com.app.rupyz.generic.model.profile.achievement.createAchievement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateAchievementResponse {

    @SerializedName("data")
    @Expose
    private CreateAchievementList data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public CreateAchievementList getData() {
        return data;
    }

    public void setData(CreateAchievementList data) {
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
