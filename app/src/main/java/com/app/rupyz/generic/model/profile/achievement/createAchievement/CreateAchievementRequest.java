package com.app.rupyz.generic.model.profile.achievement.createAchievement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateAchievementRequest {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("client")
    @Expose
    private String client;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("image")
    @Expose
    private int image;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("id")
    @Expose
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
