package com.app.rupyz.generic.model.createemi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EMIResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("account_type")
    @Expose
    private String accountType;
    @SerializedName("date_opened")
    @Expose
    private String dateOpened;
    @SerializedName("interest_rate")
    @Expose
    private Double interestRate;
    @SerializedName("installment_amount")
    @Expose
    private Double installmentAmount;
    @SerializedName("repayment_tenure")
    @Expose
    private Integer repaymentTenure;
    @SerializedName("institution_name")
    @Expose
    private String institutionName;
    @SerializedName("account_no")
    @Expose
    private String accountNo;
    @SerializedName("month_due_day")
    @Expose
    private Integer monthDueDay;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("profile")
    @Expose
    private Integer profile;
    @SerializedName("organization")
    @Expose
    private Integer organization;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("updated_by")
    @Expose
    private Object updatedBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(String dateOpened) {
        this.dateOpened = dateOpened;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Integer getRepaymentTenure() {
        return repaymentTenure;
    }

    public void setRepaymentTenure(Integer repaymentTenure) {
        this.repaymentTenure = repaymentTenure;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getMonthDueDay() {
        return monthDueDay;
    }

    public void setMonthDueDay(Integer monthDueDay) {
        this.monthDueDay = monthDueDay;
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

    public Integer getProfile() {
        return profile;
    }

    public void setProfile(Integer profile) {
        this.profile = profile;
    }

    public Integer getOrganization() {
        return organization;
    }

    public void setOrganization(Integer organization) {
        this.organization = organization;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }
}
