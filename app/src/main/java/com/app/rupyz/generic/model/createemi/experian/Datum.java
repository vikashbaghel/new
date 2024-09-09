package com.app.rupyz.generic.model.createemi.experian;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("scheduled_monthly_payment_amount")
    @Expose
    private Double scheduledMonthlyPaymentAmount;
    @SerializedName("month_due_day")
    @Expose
    private Integer monthDueDay;
    @SerializedName("subscriber_name")
    @Expose
    private String subscriberName;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("rate_of_interest")
    @Expose
    private Double rateOfInterest;
    @SerializedName("repayment_tenure")
    @Expose
    private Integer repaymentTenure;
    @SerializedName("calc_due_date")
    @Expose
    private String calcDueDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getScheduledMonthlyPaymentAmount() {
        return scheduledMonthlyPaymentAmount;
    }

    public void setScheduledMonthlyPaymentAmount(Double scheduledMonthlyPaymentAmount) {
        this.scheduledMonthlyPaymentAmount = scheduledMonthlyPaymentAmount;
    }

    public Integer getMonthDueDay() {
        return monthDueDay;
    }

    public void setMonthDueDay(Integer monthDueDay) {
        this.monthDueDay = monthDueDay;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getRateOfInterest() {
        return rateOfInterest;
    }

    public void setRateOfInterest(Double rateOfInterest) {
        this.rateOfInterest = rateOfInterest;
    }

    public Integer getRepaymentTenure() {
        return repaymentTenure;
    }

    public void setRepaymentTenure(Integer repaymentTenure) {
        this.repaymentTenure = repaymentTenure;
    }

    public String getCalcDueDate() {
        return calcDueDate;
    }

    public void setCalcDueDate(String calcDueDate) {
        this.calcDueDate = calcDueDate;
    }
}
