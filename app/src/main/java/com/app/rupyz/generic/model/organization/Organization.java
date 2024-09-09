package com.app.rupyz.generic.model.organization;

public class Organization {
    private String legal_name;
    private String email;
    private String mobile;
    private String state;
    private String city;
    private String address_line_1;
    private String address_line_2;
    private String pincode;
    private String org_type;

    public boolean isEquifax_consent() {
        return equifax_consent;
    }

    public void setEquifax_consent(boolean equifax_consent) {
        this.equifax_consent = equifax_consent;
    }

    private boolean equifax_consent;

    public String getOrg_type() {
        return org_type;
    }

    public void setOrg_type(String org_type) {
        this.org_type = org_type;
    }

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }

    public int getReg_step() {
        return reg_step;
    }

    public void setReg_step(int reg_step) {
        this.reg_step = reg_step;
    }

    private String pan_id;
    private int reg_step;

    public String getLegal_name() {
        return legal_name;
    }

    public void setLegal_name(String legal_name) {
        this.legal_name = legal_name;
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

    public String getAddress_line_1() {
        return address_line_1;
    }

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPrimary_gstin() {
        return primary_gstin;
    }

    public void setPrimary_gstin(String primary_gstin) {
        this.primary_gstin = primary_gstin;
    }

    private String primary_gstin;
}
