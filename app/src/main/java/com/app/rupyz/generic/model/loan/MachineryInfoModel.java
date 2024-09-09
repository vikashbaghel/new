package com.app.rupyz.generic.model.loan;

import java.io.Serializable;

public class MachineryInfoModel implements Serializable {
    private String loan_number, loan_amount, emi, loan_tenure, other_charge, advance_emi, deposit, interest_rate, processing_fee, insurance;
    private String otherValue, processingValue, interest_income;

    private String insuranceValue, depositValue, advanceValue, timeValue;
    private String otherChargeType, processingType, depositType, insuranceType, loanTenureType;

    boolean loanTenureMonthOrYear;

    public MachineryInfoModel(String loan_number, String loan_amount, String emi, String loan_tenure, String other_charge, String advance_emi, String deposit, String interest, String processing_fee, String insurance, String otherType, String otherValue, String processingType, String processingValue, String insuranceValue, String depositValue, String advanceValue, String timeValue, String interest_income, String otherChargeType, String processing_type, String depositType, String insuranceType, String loanTenureType) {
        this.loan_number = loan_number;
        this.loan_amount = loan_amount;
        this.emi = emi;
        this.loan_tenure = loan_tenure;
        this.other_charge = other_charge;
        this.advance_emi = advance_emi;
        this.deposit = deposit;
        this.interest_rate = interest;
        this.processing_fee = processing_fee;
        this.insurance = insurance;
        this.otherValue = otherValue;
        this.processingValue = processingValue;
        this.insuranceValue = insuranceValue;
        this.depositValue = depositValue;
        this.advanceValue = advanceValue;
        this.timeValue = timeValue;
        this.interest_income = interest_income;
        this.otherChargeType = otherChargeType;
        this.processingType = processing_type;
        this.depositType = depositType;
        this.insuranceType = insuranceType;
        this.loanTenureType = loanTenureType;
    }

    public String getLoan_amount() {
        return loan_amount;
    }

    public void setLoan_amount(String loan_amount) {
        this.loan_amount = loan_amount;
    }

    public String getEmi() {
        return emi;
    }

    public void setEmi(String emi) {
        this.emi = emi;
    }

    public String getLoan_tenure() {
        return loan_tenure;
    }

    public void setLoan_tenure(String loan_tenure) {
        this.loan_tenure = loan_tenure;
    }

    public String getOther_charge() {
        return other_charge;
    }

    public void setOther_charge(String other_charge) {
        this.other_charge = other_charge;
    }

    public String getAdvance_emi() {
        return advance_emi;
    }

    public void setAdvance_emi(String advance_emi) {
        this.advance_emi = advance_emi;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getLoan_number() {
        return loan_number;
    }

    public void setLoan_number(String loan_number) {
        this.loan_number = loan_number;
    }

    public String getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(String interest_rate) {
        this.interest_rate = interest_rate;
    }

    public String getProcessing_fee() {
        return processing_fee;
    }

    public void setProcessing_fee(String processing_fee) {
        this.processing_fee = processing_fee;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getOtherValue() {
        return otherValue;
    }

    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }

    public String getProcessingValue() {
        return processingValue;
    }

    public void setProcessingValue(String processingValue) {
        this.processingValue = processingValue;
    }

    public String getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(String insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public String getDepositValue() {
        return depositValue;
    }

    public void setDepositValue(String depositValue) {
        this.depositValue = depositValue;
    }

    public String getAdvanceValue() {
        return advanceValue;
    }

    public void setAdvanceValue(String advanceValue) {
        this.advanceValue = advanceValue;
    }

    public String getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public String getInterest_income() {
        return interest_income;
    }

    public void setInterest_income(String interest_income) {
        this.interest_income = interest_income;
    }

    public String getOtherChargeType() {
        return otherChargeType;
    }

    public void setOtherChargeType(String otherChargeType) {
        this.otherChargeType = otherChargeType;
    }

    public String getProcessingType() {
        return processingType;
    }

    public void setProcessingType(String processingType) {
        this.processingType = processingType;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getLoanTenureType() {
        return loanTenureType;
    }

    public void setLoanTenureType(String loanTenureType) {
        this.loanTenureType = loanTenureType;
    }
}
