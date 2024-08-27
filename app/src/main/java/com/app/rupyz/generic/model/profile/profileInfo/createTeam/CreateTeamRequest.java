package com.app.rupyz.generic.model.profile.profileInfo.createTeam;

import com.app.rupyz.generic.model.profile.profileInfo.createProfile.SocialMedia;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTeamRequest {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("intro")
    @Expose
    private String intro;
    @SerializedName("profile_pic")
    @Expose
    private int profilePic;

    @SerializedName("id")
    @Expose
    private int id;

    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    @SerializedName("social_links")
    @Expose
    private SocialLinks socialLinks;

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

    public int getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(int profilePic) {
        this.profilePic = profilePic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
