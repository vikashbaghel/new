package com.app.rupyz.generic.model.individual.experian;
import com.google.gson.annotations.SerializedName;
public class RelationshipDetails {
    @SerializedName("Enquiry_Reason")
    public String enquiry_Reason;
    @SerializedName("Amount_Financed")
    public String amount_Financed;
    @SerializedName("Finance_Purpose")
    public Object finance_Purpose;

    public String getEnquiry_Reason() {
        return enquiry_Reason;
    }

    public void setEnquiry_Reason(String enquiry_Reason) {
        this.enquiry_Reason = enquiry_Reason;
    }

    public String getAmount_Financed() {
        return amount_Financed;
    }

    public void setAmount_Financed(String amount_Financed) {
        this.amount_Financed = amount_Financed;
    }

    public Object getFinance_Purpose() {
        return finance_Purpose;
    }

    public void setFinance_Purpose(Object finance_Purpose) {
        this.finance_Purpose = finance_Purpose;
    }

    public CurrentOtherDetails getCurrent_Other_Details() {
        return current_Other_Details;
    }

    public void setCurrent_Other_Details(CurrentOtherDetails current_Other_Details) {
        this.current_Other_Details = current_Other_Details;
    }

    public String getDuration_Of_Agreement() {
        return duration_Of_Agreement;
    }

    public void setDuration_Of_Agreement(String duration_Of_Agreement) {
        this.duration_Of_Agreement = duration_Of_Agreement;
    }

    public CurrentApplicantDetails getCurrent_Applicant_Details() {
        return current_Applicant_Details;
    }

    public void setCurrent_Applicant_Details(CurrentApplicantDetails current_Applicant_Details) {
        this.current_Applicant_Details = current_Applicant_Details;
    }

    public CurrentApplicantAddressDetails getCurrent_Applicant_Address_Details() {
        return current_Applicant_Address_Details;
    }

    public void setCurrent_Applicant_Address_Details(CurrentApplicantAddressDetails current_Applicant_Address_Details) {
        this.current_Applicant_Address_Details = current_Applicant_Address_Details;
    }

    public Object getCurrent_Applicant_Additional_AddressDetails() {
        return current_Applicant_Additional_AddressDetails;
    }

    public void setCurrent_Applicant_Additional_AddressDetails(Object current_Applicant_Additional_AddressDetails) {
        this.current_Applicant_Additional_AddressDetails = current_Applicant_Additional_AddressDetails;
    }

    @SerializedName("Current_Other_Details")
    public CurrentOtherDetails current_Other_Details;
    @SerializedName("Duration_Of_Agreement")
    public String duration_Of_Agreement;
    @SerializedName("Current_Applicant_Details")
    public CurrentApplicantDetails current_Applicant_Details;
    @SerializedName("Current_Applicant_Address_Details")
    public CurrentApplicantAddressDetails current_Applicant_Address_Details;
    @SerializedName("Current_Applicant_Additional_AddressDetails")
    public Object current_Applicant_Additional_AddressDetails;

}
