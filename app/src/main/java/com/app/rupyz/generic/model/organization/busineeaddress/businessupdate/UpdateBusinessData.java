package com.app.rupyz.generic.model.organization.busineeaddress.businessupdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateBusinessData {
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("org_type")
    @Expose
    private Object orgType;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address_line_1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line_2")
    @Expose
    private String addressLine2;
    @SerializedName("incorporation_date")
    @Expose
    private Object incorporationDate;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("pan_id")
    @Expose
    private String panId;
    @SerializedName("primary_gstin")
    @Expose
    private String primaryGstin;
    @SerializedName("reg_step")
    @Expose
    private int regStep;

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public Object getOrgType() {
        return orgType;
    }

    public void setOrgType(Object orgType) {
        this.orgType = orgType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Object getIncorporationDate() {
        return incorporationDate;
    }

    public void setIncorporationDate(Object incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getPrimaryGstin() {
        return primaryGstin;
    }

    public void setPrimaryGstin(String primaryGstin) {
        this.primaryGstin = primaryGstin;
    }

    public int getRegStep() {
        return regStep;
    }

    public void setRegStep(int regStep) {
        this.regStep = regStep;
    }
}
