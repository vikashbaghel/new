package com.app.rupyz.generic.model.profile.testimonial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestimonialData {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user_pic")
    @Expose
    private int userPic;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("rating")
    @Expose
    private double rating;
    @SerializedName("is_published")
    @Expose
    private boolean isPublished;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("organization")
    @Expose
    private int organization;

    public String getUser_pic_url() {
        return user_pic_url;
    }

    public void setUser_pic_url(String user_pic_url) {
        this.user_pic_url = user_pic_url;
    }

    @SerializedName("user_pic_url")
    @Expose
    private String user_pic_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserPic() {
        return userPic;
    }

    public void setUserPic(int userPic) {
        this.userPic = userPic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isIsPublished() {
        return isPublished;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public int getOrganization() {
        return organization;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }
}
