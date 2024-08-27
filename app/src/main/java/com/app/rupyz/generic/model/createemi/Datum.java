package com.app.rupyz.generic.model.createemi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("installment_amount")
    @Expose
    private Double installmentAmount;
    @SerializedName("month_due_day")
    @Expose
    private Integer monthDueDay;
    @SerializedName("institution_name")
    @Expose
    private String institutionName;
    @SerializedName("account_no")
    @Expose
    private String accountNo;
    @SerializedName("calc_due_date")
    @Expose
    private String calcDueDate;
    @SerializedName("repayment_tenure")
    @Expose
    private Integer repaymentTenure;
    @SerializedName("interest_rate")
    @Expose
    private Double interestRate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Integer getMonthDueDay() {
        return monthDueDay;
    }

    public void setMonthDueDay(Integer monthDueDay) {
        this.monthDueDay = monthDueDay;
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

    public String getCalcDueDate() {
        return calcDueDate;
    }

    public void setCalcDueDate(String calcDueDate) {
        this.calcDueDate = calcDueDate;
    }

    public Integer getRepaymentTenure() {
        return repaymentTenure;
    }

    public void setRepaymentTenure(Integer repaymentTenure) {
        this.repaymentTenure = repaymentTenure;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }
}
