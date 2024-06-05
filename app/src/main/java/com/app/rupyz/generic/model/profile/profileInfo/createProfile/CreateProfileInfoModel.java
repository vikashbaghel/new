package com.app.rupyz.generic.model.profile.profileInfo.createProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateProfileInfoModel {
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
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("achievements")
    @Expose
    private List<Achievement> achievements = null;
    @SerializedName("aggregated_turnover")
    @Expose
    private String aggregatedTurnover;
    @SerializedName("nature_of_business")
    @Expose
    private List<String> natureOfBusiness = null;
    @SerializedName("cin_llpin")
    @Expose
    private String cinLlpin;
    @SerializedName("incorporation_date")
    @Expose
    private Object incorporationDate;
    @SerializedName("short_description")
    @Expose
    private Object shortDescription;
    @SerializedName("about_us")
    @Expose
    private String aboutUs;
    @SerializedName("geolocation")
    @Expose
    private List<Object> geolocation = null;
    @SerializedName("social_media")
    @Expose
    private SocialMedia socialMedia;
    @SerializedName("product_and_services")
    @Expose
    private List<String> productAndServices = null;
    @SerializedName("turnover_amount")
    @Expose
    private Double turnoverAmount;
    @SerializedName("no_of_employees")
    @Expose
    private Integer noOfEmployees;
    @SerializedName("logo_image")
    @Expose
    private Integer logoImage;

    @SerializedName("business_nature")
    @Expose
    private String businessNature;

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    @SerializedName("legal_name")
    @Expose
    private String legalName;

    @SerializedName("banner_image")
    @Expose
    private Integer bannerImage;

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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public String getAggregatedTurnover() {
        return aggregatedTurnover;
    }

    public void setAggregatedTurnover(String aggregatedTurnover) {
        this.aggregatedTurnover = aggregatedTurnover;
    }

    public List<String> getNatureOfBusiness() {
        return natureOfBusiness;
    }

    public void setNatureOfBusiness(List<String> natureOfBusiness) {
        this.natureOfBusiness = natureOfBusiness;
    }

    public String getCinLlpin() {
        return cinLlpin;
    }

    public void setCinLlpin(String cinLlpin) {
        this.cinLlpin = cinLlpin;
    }

    public Object getIncorporationDate() {
        return incorporationDate;
    }

    public void setIncorporationDate(Object incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public Object getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Object shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public List<Object> getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(List<Object> geolocation) {
        this.geolocation = geolocation;
    }

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
        this.socialMedia = socialMedia;
    }

    public List<String> getProductAndServices() {
        return productAndServices;
    }

    public void setProductAndServices(List<String> productAndServices) {
        this.productAndServices = productAndServices;
    }

    public Double getTurnoverAmount() {
        return turnoverAmount;
    }

    public void setTurnoverAmount(Double turnoverAmount) {
        this.turnoverAmount = turnoverAmount;
    }

    public Integer getNoOfEmployees() {
        return noOfEmployees;
    }

    public void setNoOfEmployees(Integer noOfEmployees) {
        this.noOfEmployees = noOfEmployees;
    }

    public Integer getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(Integer logoImage) {
        this.logoImage = logoImage;
    }

    public Integer getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(Integer bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getBusinessNature() {
        return businessNature;
    }

    public void setBusinessNature(String businessNature) {
        this.businessNature = businessNature;
    }
}
