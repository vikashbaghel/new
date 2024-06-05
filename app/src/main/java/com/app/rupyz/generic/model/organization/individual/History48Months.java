package com.app.rupyz.generic.model.organization.individual;

public class History48Months {
    private int year;
    private int month;
    private String payment_status;
    private String suit_filed_status;

    public boolean isIs_missed() {
        return is_missed;
    }

    public void setIs_missed(boolean is_missed) {
        this.is_missed = is_missed;
    }

    private boolean is_missed;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getSuit_filed_status() {
        return suit_filed_status;
    }

    public void setSuit_filed_status(String suit_filed_status) {
        this.suit_filed_status = suit_filed_status;
    }

    public String getAsset_classification() {
        return asset_classification;
    }

    public void setAsset_classification(String asset_classification) {
        this.asset_classification = asset_classification;
    }

    private String asset_classification;
}
