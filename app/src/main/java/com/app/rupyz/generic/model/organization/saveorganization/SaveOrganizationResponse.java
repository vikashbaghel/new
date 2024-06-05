package com.app.rupyz.generic.model.organization.saveorganization;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SaveOrganizationResponse implements Parcelable {

    @SerializedName("data")
    @Expose
    private SaveOrganizationData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    protected SaveOrganizationResponse(Parcel in) {
        message = in.readString();
        error = in.readByte() != 0;
    }

    public static final Creator<SaveOrganizationResponse> CREATOR = new Creator<SaveOrganizationResponse>() {
        @Override
        public SaveOrganizationResponse createFromParcel(Parcel in) {
            return new SaveOrganizationResponse(in);
        }

        @Override
        public SaveOrganizationResponse[] newArray(int size) {
            return new SaveOrganizationResponse[size];
        }
    };

    public SaveOrganizationData getData() {
        return data;
    }

    public void setData(SaveOrganizationData data) {
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeByte((byte) (error ? 1 : 0));
    }
}
