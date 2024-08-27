package com.app.rupyz.generic.model.profile.profileInfo.createTeam;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TeamInfoModel implements Parcelable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("intro")
    @Expose
    private String intro;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("profile_pic_url")
    @Expose
    private String profilePicUrl;
    @SerializedName("profile_pic")
    @Expose
    private int profilePic;
    @SerializedName("organization")
    @Expose
    private int organization;
    @SerializedName("created_by")
    @Expose
    private int createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;
    @SerializedName("social_links")
    @Expose
    private SocialLinks socialLinks;


    protected TeamInfoModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        position = in.readString();
        intro = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        profilePicUrl = in.readString();
        profilePic = in.readInt();
        organization = in.readInt();
        createdBy = in.readInt();
    }

    public static final Creator<TeamInfoModel> CREATOR = new Creator<TeamInfoModel>() {
        @Override
        public TeamInfoModel createFromParcel(Parcel in) {
            return new TeamInfoModel(in);
        }

        @Override
        public TeamInfoModel[] newArray(int size) {
            return new TeamInfoModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public int getOrganization() {
        return organization;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }


    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(position);
        dest.writeString(intro);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(profilePicUrl);
        dest.writeInt(profilePic);
        dest.writeInt(organization);
        dest.writeInt(createdBy);
    }
}
