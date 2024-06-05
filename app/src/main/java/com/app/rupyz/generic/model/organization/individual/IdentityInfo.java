package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

public class IdentityInfo {
    @SerializedName("Age")
    private Age age;
    @SerializedName("Name")
    private Name name;
    @SerializedName("Gender")
    private String gender;
    @SerializedName(" AliasName")
    private AliasName aliasName;
    @SerializedName("DateOfBirth")
    private String dateOfBirth;

    public Age getAge() {
        return age;
    }

    public void setAge(Age age) {
        this.age = age;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public AliasName getAliasName() {
        return aliasName;
    }

    public void setAliasName(AliasName aliasName) {
        this.aliasName = aliasName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public PlaceOfBirthInfo getPlaceOfBirthInfo() {
        return placeOfBirthInfo;
    }

    public void setPlaceOfBirthInfo(PlaceOfBirthInfo placeOfBirthInfo) {
        this.placeOfBirthInfo = placeOfBirthInfo;
    }

    @SerializedName("PlaceOfBirthInfo")
    private PlaceOfBirthInfo placeOfBirthInfo;
}
