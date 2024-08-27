package com.app.rupyz.generic.model.user.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("pan_id")
    @Expose
    private String panId;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("address_line_1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line_2")
    @Expose
    private String addressLine2;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("reg_step")
    @Expose
    private Integer regStep;
    @SerializedName("profile_pic")
    @Expose
    private Object profilePic;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("experian_generated")
    @Expose
    private Boolean experianGenerated;
    @SerializedName("equifax_generated")
    @Expose
    private Boolean equifaxGenerated;
    @SerializedName("preferences")
    @Expose
    private Preferences preferences;
    @SerializedName("rupyz_id")
    @Expose
    private String rupyzId;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("din_no")
    @Expose
    private String dinNo;
    @SerializedName("nationality")
    @Expose
    private Object nationality;
    @SerializedName("org_ids")
    @Expose
    private List<OrgId> orgIds = null;
    @SerializedName("experian_info")
    @Expose
    private ExperianInfo experianInfo;
    @SerializedName("equifax_info")
    @Expose
    private List<EquifaxInfo> equifaxInfo = null;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getRegStep() {
        return regStep;
    }

    public void setRegStep(Integer regStep) {
        this.regStep = regStep;
    }

    public Object getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Object profilePic) {
        this.profilePic = profilePic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getExperianGenerated() {
        return experianGenerated;
    }

    public void setExperianGenerated(Boolean experianGenerated) {
        this.experianGenerated = experianGenerated;
    }

    public Boolean getEquifaxGenerated() {
        return equifaxGenerated;
    }

    public void setEquifaxGenerated(Boolean equifaxGenerated) {
        this.equifaxGenerated = equifaxGenerated;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getRupyzId() {
        return rupyzId;
    }

    public void setRupyzId(String rupyzId) {
        this.rupyzId = rupyzId;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDinNo() {
        return dinNo;
    }

    public void setDinNo(String dinNo) {
        this.dinNo = dinNo;
    }

    public Object getNationality() {
        return nationality;
    }

    public void setNationality(Object nationality) {
        this.nationality = nationality;
    }

    public List<OrgId> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<OrgId> orgIds) {
        this.orgIds = orgIds;
    }

    public ExperianInfo getExperianInfo() {
        return experianInfo;
    }

    public void setExperianInfo(ExperianInfo experianInfo) {
        this.experianInfo = experianInfo;
    }

    public List<EquifaxInfo> getEquifaxInfo() {
        return equifaxInfo;
    }

    public void setEquifaxInfo(List<EquifaxInfo> equifaxInfo) {
        this.equifaxInfo = equifaxInfo;
    }
}
