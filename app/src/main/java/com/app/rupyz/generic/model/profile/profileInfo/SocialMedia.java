package com.app.rupyz.generic.model.profile.profileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SocialMedia {

    private String facebook;
    private String instagram;
    private String linkedin;

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    private String twitter;

}
