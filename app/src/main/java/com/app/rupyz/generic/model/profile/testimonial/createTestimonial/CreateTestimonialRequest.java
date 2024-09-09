package com.app.rupyz.generic.model.profile.testimonial.createTestimonial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTestimonialRequest {

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("rating")
    @Expose
    private double rating;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_pic")
    @Expose
    private int userPic;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("is_published")
    @Expose
    private boolean isPublished;

    @SerializedName("id")
    @Expose
    private int id;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserPic() {
        return userPic;
    }

    public void setUserPic(int userPic) {
        this.userPic = userPic;
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

    public boolean isIsPublished() {
        return isPublished;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
