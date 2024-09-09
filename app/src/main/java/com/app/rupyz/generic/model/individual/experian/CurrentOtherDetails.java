package com.app.rupyz.generic.model.individual.experian;
import com.google.gson.annotations.SerializedName;
public class CurrentOtherDetails {
    @SerializedName("Income")
    public String income;
    @SerializedName("Marital_Status")
    public Object marital_Status;
    @SerializedName("Employment_Status")
    public Object employment_Status;
    @SerializedName("Time_with_Employer")
    public Object time_with_Employer;

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public Object getMarital_Status() {
        return marital_Status;
    }

    public void setMarital_Status(Object marital_Status) {
        this.marital_Status = marital_Status;
    }

    public Object getEmployment_Status() {
        return employment_Status;
    }

    public void setEmployment_Status(Object employment_Status) {
        this.employment_Status = employment_Status;
    }

    public Object getTime_with_Employer() {
        return time_with_Employer;
    }

    public void setTime_with_Employer(Object time_with_Employer) {
        this.time_with_Employer = time_with_Employer;
    }

    public Object getNumber_of_Major_Credit_Card_Held() {
        return number_of_Major_Credit_Card_Held;
    }

    public void setNumber_of_Major_Credit_Card_Held(Object number_of_Major_Credit_Card_Held) {
        this.number_of_Major_Credit_Card_Held = number_of_Major_Credit_Card_Held;
    }

    @SerializedName("Number_of_Major_Credit_Card_Held")
    public Object number_of_Major_Credit_Card_Held;
}
