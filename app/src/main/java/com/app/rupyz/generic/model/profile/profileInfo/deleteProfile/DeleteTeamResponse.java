package com.app.rupyz.generic.model.profile.profileInfo.deleteProfile;

import com.app.rupyz.generic.model.profile.profileInfo.createTeam.TeamInfoModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteTeamResponse {
    @SerializedName("data")
    @Expose
    private TeamInfoModel data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    public TeamInfoModel getData() {
        return data;
    }

    public void setData(TeamInfoModel data) {
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
