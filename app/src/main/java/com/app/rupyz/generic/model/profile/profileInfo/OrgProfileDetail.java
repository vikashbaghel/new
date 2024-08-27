package com.app.rupyz.generic.model.profile.profileInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrgProfileDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("pan_id")
    @Expose
    private String panId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("primary_gstin")
    @Expose
    private String primaryGstin;
    @SerializedName("consent_by")
    @Expose
    private String consentBy;
    @SerializedName("reg_step")
    @Expose
    private Integer regStep;
    @SerializedName("org_type")
    @Expose
    private String orgType;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("address_line_1")
    @Expose
    private String addressLine1;
    @SerializedName("registered_address")
    @Expose
    private String registered_address;
    @SerializedName("address_line_2")
    @Expose
    private String addressLine2;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("legal_name")
    @Expose
    private String legalName;
    @SerializedName("trade_name")
    @Expose
    private String tradeName;
    @SerializedName("authorized_signatories")
    @Expose
    private List<String> authorizedSignatories = null;
    @SerializedName("slug")
    @Expose
    private String slug;
    @SerializedName("gstin_status")
    @Expose
    private String gstinStatus;
    @SerializedName("status")
    @Expose
    private String Status;
    @SerializedName("gstin_date_of_registration")
    @Expose
    private String gstinDateOfRegistration;
    @SerializedName("aggregated_turnover")
    @Expose
    private String aggregatedTurnover;
    @SerializedName("entity_type")
    @Expose
    private String entityType;
//    @SerializedName("nature_of_business")
//    @Expose
//    private List<String> natureOfBusiness = null;

    @SerializedName("selected_authorized_signatory")
    @Expose
    private String selectedAuthorizedSignatory;


    public String getRegistered_address() {
        return registered_address;
    }

    public void setRegistered_address(String registered_address) {
        this.registered_address = registered_address;
    }

    public String getBusinessNature() {
        return businessNature;
    }

    public void setBusinessNature(String businessNature) {
        this.businessNature = businessNature;
    }

    @SerializedName("business_nature")
    @Expose
    private String businessNature;

    @SerializedName("cin_llpin")
    @Expose
    private String cinLlpin;
    @SerializedName("incorporation_date")
    @Expose
    private String incorporationDate;
    @SerializedName("equifax_score")
    @Expose
    private Integer equifaxScore;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("about_us")
    @Expose
    private String aboutUs;
    @SerializedName("first_name")
    @Expose
    private String first_name;
    @SerializedName("last_name")
    @Expose
    private String last_name;
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
    @SerializedName("images_count")
    @Expose
    private Integer imagesCount;
    @SerializedName("key_people")
    @Expose
    private List<KeyPerson> keyPeople = null;
    @SerializedName("key_projects")
    @Expose
    private List<Object> keyProjects = null;
    @SerializedName("compliance_rating")
    @Expose
    private Double complianceRating;
    @SerializedName("followers")
    @Expose
    private Integer followers;
    @SerializedName("following")
    @Expose
    private Integer following;
    @SerializedName("equifax_consent")
    @Expose
    private Boolean equifaxConsent;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    @SerializedName("logo_image_url")
    @Expose
    private String logo_image_url;


    @SerializedName("banner_image_url")
    @Expose
    private String banner_image_url;

    @SerializedName("logo_image")
    @Expose
    private Integer logoImage;
    @SerializedName("banner_image")
    @Expose
    private Integer bannerImage;


    /*public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    @SerializedName("banner_image_url")
    @Expose
    private String bannerImageUrl;*/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPanId() {
        return panId;
    }

    public void setPanId(String panId) {
        this.panId = panId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrimaryGstin() {
        return primaryGstin;
    }

    public void setPrimaryGstin(String primaryGstin) {
        this.primaryGstin = primaryGstin;
    }

    public String getConsentBy() {
        return consentBy;
    }

    public void setConsentBy(String consentBy) {
        this.consentBy = consentBy;
    }

    public Integer getRegStep() {
        return regStep;
    }

    public void setRegStep(Integer regStep) {
        this.regStep = regStep;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
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

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public List<String> getAuthorizedSignatories() {
        return authorizedSignatories;
    }

    public void setAuthorizedSignatories(List<String> authorizedSignatories) {
        this.authorizedSignatories = authorizedSignatories;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getGstinStatus() {
        return gstinStatus;
    }

    public void setGstinStatus(String gstinStatus) {
        this.gstinStatus = gstinStatus;
    }

    public String getGstinDateOfRegistration() {
        return gstinDateOfRegistration;
    }

    public void setGstinDateOfRegistration(String gstinDateOfRegistration) {
        this.gstinDateOfRegistration = gstinDateOfRegistration;
    }

    public String getAggregatedTurnover() {
        return aggregatedTurnover;
    }

    public void setAggregatedTurnover(String aggregatedTurnover) {
        this.aggregatedTurnover = aggregatedTurnover;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

//    public List<String> getNatureOfBusiness() {
//        return natureOfBusiness;
//    }
//
//    public void setNatureOfBusiness(List<String> natureOfBusiness) {
//        this.natureOfBusiness = natureOfBusiness;
//    }

    public String getSelectedAuthorizedSignatory() {
        return selectedAuthorizedSignatory;
    }

    public void setSelectedAuthorizedSignatory(String selectedAuthorizedSignatory) {
        this.selectedAuthorizedSignatory = selectedAuthorizedSignatory;
    }

    public String getCinLlpin() {
        return cinLlpin;
    }

    public void setCinLlpin(String cinLlpin) {
        this.cinLlpin = cinLlpin;
    }

    public String getIncorporationDate() {
        return incorporationDate;
    }

    public void setIncorporationDate(String incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public Integer getEquifaxScore() {
        return equifaxScore;
    }

    public void setEquifaxScore(Integer equifaxScore) {
        this.equifaxScore = equifaxScore;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
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

    public Integer getImagesCount() {
        return imagesCount;
    }

    public void setImagesCount(Integer imagesCount) {
        this.imagesCount = imagesCount;
    }

    public List<KeyPerson> getKeyPeople() {
        return keyPeople;
    }

    public void setKeyPeople(List<KeyPerson> keyPeople) {
        this.keyPeople = keyPeople;
    }

    public List<Object> getKeyProjects() {
        return keyProjects;
    }

    public void setKeyProjects(List<Object> keyProjects) {
        this.keyProjects = keyProjects;
    }

    public Double getComplianceRating() {
        return complianceRating;
    }

    public void setComplianceRating(Double complianceRating) {
        this.complianceRating = complianceRating;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public Boolean getEquifaxConsent() {
        return equifaxConsent;
    }

    public void setEquifaxConsent(Boolean equifaxConsent) {
        this.equifaxConsent = equifaxConsent;
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

    public String getRegisteredAddress() {
        return registered_address;
    }

    public void setRegisteredAddress(String registered_address) {
        this.registered_address = registered_address;
    }

    public String getLogo_image_url() {
        return logo_image_url;
    }

    public void setLogo_image_url(String logo_image_url) {
        this.logo_image_url = logo_image_url;
    }

    public String getBanner_image_url() {
        return banner_image_url;
    }

    public void setBanner_image_url(String banner_image_url) {
        this.banner_image_url = banner_image_url;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String fst_name) {
        first_name = fst_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String ls_name) {
        last_name = ls_name;
    }



}
