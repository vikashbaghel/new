package com.app.rupyz.generic.model.organization.individual;

public class EnquiryHistory {
    private String date;
    private String time;
    private int amount;
    private String institution;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getRequestpurpose() {
        return requestpurpose;
    }

    public void setRequestpurpose(String requestpurpose) {
        this.requestpurpose = requestpurpose;
    }

    private String requestpurpose;
}
