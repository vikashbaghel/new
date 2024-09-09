package com.app.rupyz.generic.model.individual.experian;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tradeline {
    @SerializedName("Income")
    public Object income;
    @SerializedName("Open_Date")
    public String open_Date;
    @SerializedName("Date_Closed")
    public String date_Closed;
    @SerializedName("OnTime_Payment")
    public String ontime_payment;

    public String getOntime_payment() {
        return ontime_payment;
    }

    public void setOntime_payment(String ontime_payment) {
        this.ontime_payment = ontime_payment;
    }

    public boolean isNegative_account() {
        return negative_account;
    }

    public void setNegative_account(boolean negative_account) {
        this.negative_account = negative_account;
    }

    public String getAsset_classification() {
        return asset_classification;
    }

    public void setAsset_classification(String asset_classification) {
        this.asset_classification = asset_classification;
    }

    public String getDelayed_payment() {
        return delayed_payment;
    }

    public void setDelayed_payment(String delayed_payment) {
        this.delayed_payment = delayed_payment;
    }

    @SerializedName("Negative_Account")
    public boolean negative_account;
    @SerializedName("Asset_Classification")
    public String asset_classification;
    @SerializedName("Delayed_Payment")
    public String delayed_payment;
    @SerializedName("Account_Type")
    public String account_Type;
    @SerializedName("CurrencyCode")
    public String currencyCode;
    @SerializedName("Date_Reported")
    public String date_Reported;
    @SerializedName("Account_Number")
    public String account_Number;
    @SerializedName("Account_Status")
    public String account_Status;
    @SerializedName("DateOfAddition")
    public String dateOfAddition;
    @SerializedName("Payment_Rating")
    public String payment_Rating;
    @SerializedName("Portfolio_Type")
    public String portfolio_Type;
    @SerializedName("Terms_Duration")
    public Object terms_Duration;
    @SerializedName("Amount_Past_Due")
    public String amount_Past_Due;
    @SerializedName("Current_Balance")
    public String current_Balance;
    @SerializedName("Occupation_Code")
    public String occupation_Code;
    @SerializedName("Special_Comment")
    public Object special_Comment;
    @SerializedName("Subscriber_Name")
    public String subscriber_Name;
    @SerializedName("Terms_Frequency")
    public Object terms_Frequency;
    @SerializedName("Income_Indicator")
    public Object income_Indicator;
    @SerializedName("Rate_of_Interest")
    public Double rate_of_Interest;
    @SerializedName("Repayment_Tenure")
    public Integer repayment_Tenure;
    @SerializedName("Consumer_comments")
    public Object consumer_comments;
    @SerializedName("DefaultStatusDate")
    public Object defaultStatusDate;
    @SerializedName("Settlement_Amount")
    public String settlement_Amount;
    @SerializedName("Type_of_Collateral")
    public Object type_of_Collateral;
    @SerializedName("WriteOffStatusDate")
    public Object writeOffStatusDate;
    @SerializedName("CAIS_Holder_Details")
    public Object cAIS_Holder_Details;
    @SerializedName("Credit_Limit_Amount")
    public String credit_Limit_Amount;
    @SerializedName("Subscriber_comments")
    public Object subscriber_comments;
    @SerializedName("Value_of_Collateral")
    public Object value_of_Collateral;
    @SerializedName("CAIS_Account_History")
    public List<CAISAccountHistory> cAIS_Account_History;
    @SerializedName("CAIS_Holder_Address_Details")
    public List<CAISHolderAddressDetails> cAIS_Holder_Address_Details;
    @SerializedName("month_due_day")
    public Integer monthDueDay;

    public Object getIncome() {
        return income;
    }

    public void setIncome(Object income) {
        this.income = income;
    }

    public String getOpen_Date() {
        return open_Date;
    }

    public void setOpen_Date(String open_Date) {
        this.open_Date = open_Date;
    }

    public String getDate_Closed() {
        return date_Closed;
    }

    public void setDate_Closed(String date_Closed) {
        this.date_Closed = date_Closed;
    }

    public String getAccount_Type() {
        return account_Type;
    }

    public void setAccount_Type(String account_Type) {
        this.account_Type = account_Type;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDate_Reported() {
        return date_Reported;
    }

    public void setDate_Reported(String date_Reported) {
        this.date_Reported = date_Reported;
    }

    public String getAccount_Number() {
        return account_Number;
    }

    public void setAccount_Number(String account_Number) {
        this.account_Number = account_Number;
    }

    public String getAccount_Status() {
        return account_Status;
    }

    public void setAccount_Status(String account_Status) {
        this.account_Status = account_Status;
    }

    public String getDateOfAddition() {
        return dateOfAddition;
    }

    public void setDateOfAddition(String dateOfAddition) {
        this.dateOfAddition = dateOfAddition;
    }

    public String getPayment_Rating() {
        return payment_Rating;
    }

    public void setPayment_Rating(String payment_Rating) {
        this.payment_Rating = payment_Rating;
    }

    public String getPortfolio_Type() {
        return portfolio_Type;
    }

    public void setPortfolio_Type(String portfolio_Type) {
        this.portfolio_Type = portfolio_Type;
    }

    public Object getTerms_Duration() {
        return terms_Duration;
    }

    public void setTerms_Duration(Object terms_Duration) {
        this.terms_Duration = terms_Duration;
    }

    public String getAmount_Past_Due() {
        return amount_Past_Due;
    }

    public void setAmount_Past_Due(String amount_Past_Due) {
        this.amount_Past_Due = amount_Past_Due;
    }

    public String getCurrent_Balance() {
        return current_Balance;
    }

    public void setCurrent_Balance(String current_Balance) {
        this.current_Balance = current_Balance;
    }

    public String getOccupation_Code() {
        return occupation_Code;
    }

    public void setOccupation_Code(String occupation_Code) {
        this.occupation_Code = occupation_Code;
    }

    public Object getSpecial_Comment() {
        return special_Comment;
    }

    public void setSpecial_Comment(Object special_Comment) {
        this.special_Comment = special_Comment;
    }

    public String getSubscriber_Name() {
        return subscriber_Name;
    }

    public void setSubscriber_Name(String subscriber_Name) {
        this.subscriber_Name = subscriber_Name;
    }

    public Object getTerms_Frequency() {
        return terms_Frequency;
    }

    public void setTerms_Frequency(Object terms_Frequency) {
        this.terms_Frequency = terms_Frequency;
    }

    public Object getIncome_Indicator() {
        return income_Indicator;
    }

    public void setIncome_Indicator(Object income_Indicator) {
        this.income_Indicator = income_Indicator;
    }

    public Double getRate_of_Interest() {
        return rate_of_Interest;
    }

    public void setRate_of_Interest(Double rate_of_Interest) {
        this.rate_of_Interest = rate_of_Interest;
    }

    public Integer getRepayment_Tenure() {
        return repayment_Tenure;
    }

    public void setRepayment_Tenure(Integer repayment_Tenure) {
        this.repayment_Tenure = repayment_Tenure;
    }

    public Object getConsumer_comments() {
        return consumer_comments;
    }

    public void setConsumer_comments(Object consumer_comments) {
        this.consumer_comments = consumer_comments;
    }

    public Object getDefaultStatusDate() {
        return defaultStatusDate;
    }

    public void setDefaultStatusDate(Object defaultStatusDate) {
        this.defaultStatusDate = defaultStatusDate;
    }

    public String getSettlement_Amount() {
        return settlement_Amount;
    }

    public void setSettlement_Amount(String settlement_Amount) {
        this.settlement_Amount = settlement_Amount;
    }

    public Object getType_of_Collateral() {
        return type_of_Collateral;
    }

    public void setType_of_Collateral(Object type_of_Collateral) {
        this.type_of_Collateral = type_of_Collateral;
    }

    public Object getWriteOffStatusDate() {
        return writeOffStatusDate;
    }

    public void setWriteOffStatusDate(Object writeOffStatusDate) {
        this.writeOffStatusDate = writeOffStatusDate;
    }

    public Object getcAIS_Holder_Details() {
        return cAIS_Holder_Details;
    }

    public void setcAIS_Holder_Details(Object cAIS_Holder_Details) {
        this.cAIS_Holder_Details = cAIS_Holder_Details;
    }

    public String getCredit_Limit_Amount() {
        return credit_Limit_Amount;
    }

    public void setCredit_Limit_Amount(String credit_Limit_Amount) {
        this.credit_Limit_Amount = credit_Limit_Amount;
    }

    public Object getSubscriber_comments() {
        return subscriber_comments;
    }

    public void setSubscriber_comments(Object subscriber_comments) {
        this.subscriber_comments = subscriber_comments;
    }

    public Object getValue_of_Collateral() {
        return value_of_Collateral;
    }

    public void setValue_of_Collateral(Object value_of_Collateral) {
        this.value_of_Collateral = value_of_Collateral;
    }

    public List<CAISAccountHistory> getcAIS_Account_History() {
        return cAIS_Account_History;
    }

    public void setcAIS_Account_History(List<CAISAccountHistory> cAIS_Account_History) {
        this.cAIS_Account_History = cAIS_Account_History;
    }

    public String getDate_of_Last_Payment() {
        return date_of_Last_Payment;
    }

    public void setDate_of_Last_Payment(String date_of_Last_Payment) {
        this.date_of_Last_Payment = date_of_Last_Payment;
    }

    public Object getLitigationStatusDate() {
        return litigationStatusDate;
    }

    public void setLitigationStatusDate(Object litigationStatusDate) {
        this.litigationStatusDate = litigationStatusDate;
    }

    public String getAccountHoldertypeCode() {
        return accountHoldertypeCode;
    }

    public void setAccountHoldertypeCode(String accountHoldertypeCode) {
        this.accountHoldertypeCode = accountHoldertypeCode;
    }

    public Object getIdentification_Number() {
        return identification_Number;
    }

    public void setIdentification_Number(Object identification_Number) {
        this.identification_Number = identification_Number;
    }

    public Object getPromotional_Rate_Flag() {
        return promotional_Rate_Flag;
    }

    public void setPromotional_Rate_Flag(Object promotional_Rate_Flag) {
        this.promotional_Rate_Flag = promotional_Rate_Flag;
    }

    public String getWritten_Off_Amt_Total() {
        return written_Off_Amt_Total;
    }

    public void setWritten_Off_Amt_Total(String written_Off_Amt_Total) {
        this.written_Off_Amt_Total = written_Off_Amt_Total;
    }

    public String getPayment_History_Profile() {
        return payment_History_Profile;
    }

    public void setPayment_History_Profile(String payment_History_Profile) {
        this.payment_History_Profile = payment_History_Profile;
    }

    public Object getSuitFiled_WilfulDefault() {
        return suitFiled_WilfulDefault;
    }

    public void setSuitFiled_WilfulDefault(Object suitFiled_WilfulDefault) {
        this.suitFiled_WilfulDefault = suitFiled_WilfulDefault;
    }

    public List<CAISHolderPhoneDetails> getcAIS_Holder_Phone_Details() {
        return cAIS_Holder_Phone_Details;
    }

    public void setcAIS_Holder_Phone_Details(List<CAISHolderPhoneDetails> cAIS_Holder_Phone_Details) {
        this.cAIS_Holder_Phone_Details = cAIS_Holder_Phone_Details;
    }

    public Object getDate_of_First_Delinquency() {
        return date_of_First_Delinquency;
    }

    public void setDate_of_First_Delinquency(Object date_of_First_Delinquency) {
        this.date_of_First_Delinquency = date_of_First_Delinquency;
    }

    public String getWritten_Off_Amt_Principal() {
        return written_Off_Amt_Principal;
    }

    public void setWritten_Off_Amt_Principal(String written_Off_Amt_Principal) {
        this.written_Off_Amt_Principal = written_Off_Amt_Principal;
    }

    public Object getIncome_Frequency_Indicator() {
        return income_Frequency_Indicator;
    }

    public void setIncome_Frequency_Indicator(Object income_Frequency_Indicator) {
        this.income_Frequency_Indicator = income_Frequency_Indicator;
    }

    public Object getOriginal_Charge_Off_Amount() {
        return original_Charge_Off_Amount;
    }

    public void setOriginal_Charge_Off_Amount(Object original_Charge_Off_Amount) {
        this.original_Charge_Off_Amount = original_Charge_Off_Amount;
    }

    public Object getWritten_off_Settled_Status() {
        return written_off_Settled_Status;
    }

    public void setWritten_off_Settled_Status(Object written_off_Settled_Status) {
        this.written_off_Settled_Status = written_off_Settled_Status;
    }

    public Object getValue_of_Credits_Last_Month() {
        return value_of_Credits_Last_Month;
    }

    public void setValue_of_Credits_Last_Month(Object value_of_Credits_Last_Month) {
        this.value_of_Credits_Last_Month = value_of_Credits_Last_Month;
    }

    public Double getScheduled_Monthly_Payment_Amount() {
        return scheduled_Monthly_Payment_Amount;
    }

    public void setScheduled_Monthly_Payment_Amount(Double scheduled_Monthly_Payment_Amount) {
        this.scheduled_Monthly_Payment_Amount = scheduled_Monthly_Payment_Amount;
    }

    public String getHighest_Credit_or_Original_Loan_Amount() {
        return highest_Credit_or_Original_Loan_Amount;
    }

    public void setHighest_Credit_or_Original_Loan_Amount(String highest_Credit_or_Original_Loan_Amount) {
        this.highest_Credit_or_Original_Loan_Amount = highest_Credit_or_Original_Loan_Amount;
    }

    public Object getSuitFiledWillfulDefaultWrittenOffStatus() {
        return suitFiledWillfulDefaultWrittenOffStatus;
    }

    public void setSuitFiledWillfulDefaultWrittenOffStatus(Object suitFiledWillfulDefaultWrittenOffStatus) {
        this.suitFiledWillfulDefaultWrittenOffStatus = suitFiledWillfulDefaultWrittenOffStatus;
    }

    public List<CAISHolderAddressDetails> getcAIS_Holder_Address_Details() {
        return cAIS_Holder_Address_Details;
    }

    public void setcAIS_Holder_Address_Details(List<CAISHolderAddressDetails> cAIS_Holder_Address_Details) {
        this.cAIS_Holder_Address_Details = cAIS_Holder_Address_Details;
    }

    @SerializedName("Date_of_Last_Payment")
    public String date_of_Last_Payment;
    @SerializedName("LitigationStatusDate")
    public Object litigationStatusDate;
    @SerializedName("AccountHoldertypeCode")
    public String accountHoldertypeCode;
    @SerializedName("Identification_Number")
    public Object identification_Number;
    @SerializedName("Promotional_Rate_Flag")
    public Object promotional_Rate_Flag;
    @SerializedName("Written_Off_Amt_Total")
    public String written_Off_Amt_Total;
    @SerializedName("Payment_History_Profile")
    public String payment_History_Profile;
    @SerializedName("SuitFiled_WilfulDefault")
    public Object suitFiled_WilfulDefault;
    @SerializedName("CAIS_Holder_Phone_Details")
    public List<CAISHolderPhoneDetails> cAIS_Holder_Phone_Details;
    @SerializedName("Date_of_First_Delinquency")
    public Object date_of_First_Delinquency;
    @SerializedName("Written_Off_Amt_Principal")
    public String written_Off_Amt_Principal;
    @SerializedName("Income_Frequency_Indicator")
    public Object income_Frequency_Indicator;
    @SerializedName("Original_Charge_Off_Amount")
    public Object original_Charge_Off_Amount;
    @SerializedName("Written_off_Settled_Status")
    public Object written_off_Settled_Status;
    @SerializedName("Value_of_Credits_Last_Month")
    public Object value_of_Credits_Last_Month;
    @SerializedName("Scheduled_Monthly_Payment_Amount")
    public Double scheduled_Monthly_Payment_Amount;
    @SerializedName("Highest_Credit_or_Original_Loan_Amount")
    public String highest_Credit_or_Original_Loan_Amount;
    @SerializedName("SuitFiledWillfulDefaultWrittenOffStatus")
    public Object suitFiledWillfulDefaultWrittenOffStatus;

    public Integer getMonthDueDay() {
        return monthDueDay;
    }

    public void setMonthDueDay(Integer monthDueDay) {
        this.monthDueDay = monthDueDay;
    }
}
