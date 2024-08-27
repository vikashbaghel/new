package com.app.rupyz.generic.model.profile.profileInfo.team;

import com.app.rupyz.generic.model.profile.profileInfo.createTeam.TeamInfoModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TeamInfo {
    @SerializedName("data")
    @Expose
    private List<TeamInfoModel> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public List<TeamInfoModel> getData() {
        return data;
    }

    public void setData(List<TeamInfoModel> data) {
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
