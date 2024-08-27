package com.app.rupyz.generic.model.createemi;

public class CreateEMI {

    private String open_date;
    private Double rate_of_interest;
    private Double scheduled_monthly_payment_amount;
    private Integer repayment_tenure;
    private String subscriber_name;
    private String account_number;
    private Integer month_due_day;

    public CreateEMI() {
    }

    public String getOpen_date() {
        return open_date;
    }

    public void setOpen_date(String open_date) {
        this.open_date = open_date;
    }

    public Double getRate_of_interest() {
        return rate_of_interest;
    }

    public void setRate_of_interest(Double rate_of_interest) {
        this.rate_of_interest = rate_of_interest;
    }

    public Double getScheduled_monthly_payment_amount() {
        return scheduled_monthly_payment_amount;
    }

    public void setScheduled_monthly_payment_amount(Double scheduled_monthly_payment_amount) {
        this.scheduled_monthly_payment_amount = scheduled_monthly_payment_amount;
    }

    public Integer getRepayment_tenure() {
        return repayment_tenure;
    }

    public void setRepayment_tenure(Integer repayment_tenure) {
        this.repayment_tenure = repayment_tenure;
    }

    public String getSubscriber_name() {
        return subscriber_name;
    }

    public void setSubscriber_name(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public Integer getMonth_due_day() {
        return month_due_day;
    }

    public void setMonth_due_day(Integer month_due_day) {
        this.month_due_day = month_due_day;
    }
}
