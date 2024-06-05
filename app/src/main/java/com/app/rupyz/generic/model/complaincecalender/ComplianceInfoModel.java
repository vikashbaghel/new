package com.app.rupyz.generic.model.complaincecalender;

public class ComplianceInfoModel {
    private int id;
    private String title, description, due_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return due_date;
    }

    public void setDate(String due_date) {
        this.due_date = due_date;
    }
}
