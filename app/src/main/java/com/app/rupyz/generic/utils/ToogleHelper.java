package com.app.rupyz.generic.utils;

public class ToogleHelper {

    //String tenureValue;
    String tenureType, depositType, principal, processingType, insuranceType, otherChargeType;
    double tenureValue, depositValue, processingValue, insuranceValue, otherChargeValue, interestIncomeValue;

    private static final ToogleHelper ourInstance = new ToogleHelper();

    public static ToogleHelper getInstance() {
        return ourInstance;
    }

    private ToogleHelper() {

    }
    /*public void setText(String editValue) {
        this.tenureValue = editValue;
    }
    public String getText() {
        return tenureValue;
    }*/

    public void setTenureValue(String tenureValue) {
        this.tenureValue = Double.valueOf(tenureValue);
    }

    public double getTenureValue() {
        if (getTenureType().equals("MONTH")) {
            return tenureValue;
        } else if(getTenureType().equals("YEAR")){
            return tenureValue * 12;
        } else {
            return 0;
        }
    }

    public void setTenureType(String tenureType) {
        this.tenureType = tenureType;
    }

    public String getTenureType() {
        return tenureType;
    }

    public void setDepositValue(String depositValue) {
        this.depositValue = Double.valueOf(depositValue);
    }

    public double getDepositValue() {
        if (!StringUtils.isBlank(getDepositType()) && getDepositType().equals("Amount")) {
            return depositValue * 100 / Double.parseDouble(getPrincipal());
        } else if(!StringUtils.isBlank(getDepositType()) && getDepositType().equals("Percentage")){
            return depositValue;
        } else {
            return 0;
        }
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsuranceType() {
        return insuranceType;
    }


    public void setInsuranceValue(String insuranceValue) {
        this.insuranceValue = Double.valueOf(insuranceValue);
    }

    public double getInsuranceValue() {
        if (!StringUtils.isBlank(getInsuranceType()) && getInsuranceType().equals("Amount")) {
            return insuranceValue;
        } else if(!StringUtils.isBlank(getInsuranceType()) && getInsuranceType().equals("Percentage")) {
            return Double.parseDouble(getPrincipal()) * insuranceValue / 100;
        } else {
            return 0;
        }
    }

    public double getShowInsurance(){
        if (!StringUtils.isBlank(getInsuranceType()) && getInsuranceType().equals("Amount")) {
            return insuranceValue;
        } else if(!StringUtils.isBlank(getInsuranceType()) && getInsuranceType().equals("Percentage")){
            return insuranceValue;
        } else {
            return 0;
        }
    }


    public void setProcessingType(String processingType) {
        this.processingType = processingType;
    }

    public String getProcessingType() {
        return processingType;
    }


    public void setProcessingValue(String processingValue) {
        this.processingValue = Double.valueOf(processingValue);
    }

    public double getProcessingValue() {
        if (!StringUtils.isBlank(getProcessingType()) && getProcessingType().equals("Amount")) {
            return processingValue;
        } else if(!StringUtils.isBlank(getProcessingType()) && getProcessingType().equals("Percentage")) {
            return Double.parseDouble(getPrincipal()) * processingValue / 100;
        } else {
            return 0;
        }
    }


    public void setOtherChargeType(String otherChargeType) {
        this.otherChargeType = otherChargeType;
    }

    public String getOtherChargeType() {
        return otherChargeType;
    }


    public void setOtherChargeValue(String otherChargeValue) {
        this.otherChargeValue = Double.valueOf(otherChargeValue);
    }

    public double getOtherChargeValue() {
        if (!StringUtils.isBlank(getOtherChargeType()) && getOtherChargeType().equals("Amount")) {
            return otherChargeValue;
        } else if(!StringUtils.isBlank(getOtherChargeType()) && getOtherChargeType().equals("Percentage")){
            return Double.parseDouble(getPrincipal()) * otherChargeValue / 100;
        } else {
            return 0;
        }
    }

    public double getShowOtherCharge(){
        if (!StringUtils.isBlank(getOtherChargeType()) && getOtherChargeType().equals("Amount")) {
            return otherChargeValue;
        } else if(!StringUtils.isBlank(getOtherChargeType()) && getOtherChargeType().equals("Percentage")){
            return otherChargeValue;
        } else {
            return 0;
        }
    }

    public double getShowProcessingFee(){
        if (!StringUtils.isBlank(getProcessingType())) {
            return processingValue;
        } else {
            return 0;
        }
    }

    public void setInterestIncomeValue(String interestIncomeValue) {
        this.interestIncomeValue = Double.valueOf(interestIncomeValue);
    }

    public double getInterestIncomeValue() {
        return interestIncomeValue;
    }

}
