package com.app.rupyz.generic.model.individual.experian;

import com.google.gson.annotations.SerializedName;

public class CAISAccountHistory {
    @SerializedName("Year")
    public String year;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDays_Past_Due() {
        return days_Past_Due;
    }

    public void setDays_Past_Due(String days_Past_Due) {
        this.days_Past_Due = days_Past_Due;
    }

    public String getAsset_Classification() {
        return asset_Classification;
    }

    public void setAsset_Classification(String asset_Classification) {
        this.asset_Classification = asset_Classification;
    }

    @SerializedName("Month")
    public String month;
    @SerializedName("Days_Past_Due")
    public String days_Past_Due;
    @SerializedName("Asset_Classification")
    public String asset_Classification;

}
