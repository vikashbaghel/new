package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class Name {
    @SerializedName("FullName")
    private String fullName;
    @SerializedName("LastName")
    private String lastName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @SerializedName("FirstName")
    private String firstName;
}
