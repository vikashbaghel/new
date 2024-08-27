package com.app.rupyz.generic.model.profile.profileInfo.createTeam;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTeamResponse implements Parcelable {

    @SerializedName("data")
    @Expose
    private TeamInfoModel data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    protected CreateTeamResponse(Parcel in) {
        message = in.readString();
        error = in.readByte() != 0;
    }

    public static final Creator<CreateTeamResponse> CREATOR = new Creator<CreateTeamResponse>() {
        @Override
        public CreateTeamResponse createFromParcel(Parcel in) {
            return new CreateTeamResponse(in);
        }

        @Override
        public CreateTeamResponse[] newArray(int size) {
            return new CreateTeamResponse[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeByte((byte) (error ? 1 : 0));
    }
}
