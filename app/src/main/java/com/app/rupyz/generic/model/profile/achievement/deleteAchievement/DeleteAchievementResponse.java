package com.app.rupyz.generic.model.profile.achievement.deleteAchievement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteAchievementResponse {
    @SerializedName("data")
    @Expose
    private DeleteAchievementData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public DeleteAchievementData getData() {
        return data;
    }

    public void setData(DeleteAchievementData data) {
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
