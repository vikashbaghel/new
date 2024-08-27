package com.app.rupyz.generic.model.profile.testimonial.createTestimonial;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTestimonialResponse implements Parcelable {

    @SerializedName("data")
    @Expose
    private TestimonialData data;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error")
    @Expose
    private boolean error;

    protected CreateTestimonialResponse(Parcel in) {
        message = in.readString();
        error = in.readByte() != 0;
    }

    public static final Creator<CreateTestimonialResponse> CREATOR = new Creator<CreateTestimonialResponse>() {
        @Override
        public CreateTestimonialResponse createFromParcel(Parcel in) {
            return new CreateTestimonialResponse(in);
        }

        @Override
        public CreateTestimonialResponse[] newArray(int size) {
            return new CreateTestimonialResponse[size];
        }
    };

    public TestimonialData getData() {
        return data;
    }

    public void setData(TestimonialData data) {
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
