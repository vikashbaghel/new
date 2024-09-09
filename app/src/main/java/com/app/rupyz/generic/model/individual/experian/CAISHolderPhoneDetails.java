package com.app.rupyz.generic.model.individual.experian;

import com.google.gson.annotations.SerializedName;

public class CAISHolderPhoneDetails {
    @SerializedName("Telephone_Number")
    public String Telephone_Number;
    @SerializedName("Telephone_Type")
    public String Telephone_Type;
    @SerializedName("Telephone_Extension")
    public String Telephone_Extension;
    @SerializedName("Mobile_Telephone_Number")
    public String Mobile_Telephone_Number;
    @SerializedName("FaxNumber")
    public String FaxNumber;
    @SerializedName("EMailId")
    public String EMailId;

    public String getTelephone_Number() {
        return Telephone_Number;
    }

    public void setTelephone_Number(String telephone_Number) {
        Telephone_Number = telephone_Number;
    }

    public String getTelephone_Type() {
        return Telephone_Type;
    }

    public void setTelephone_Type(String telephone_Type) {
        Telephone_Type = telephone_Type;
    }

    public String getTelephone_Extension() {
        return Telephone_Extension;
    }

    public void setTelephone_Extension(String telephone_Extension) {
        Telephone_Extension = telephone_Extension;
    }

    public String getMobile_Telephone_Number() {
        return Mobile_Telephone_Number;
    }

    public void setMobile_Telephone_Number(String mobile_Telephone_Number) {
        Mobile_Telephone_Number = mobile_Telephone_Number;
    }

    public String getFaxNumber() {
        return FaxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        FaxNumber = faxNumber;
    }

    public String getEMailId() {
        return EMailId;
    }

    public void setEMailId(String EMailId) {
        this.EMailId = EMailId;
    }
}
