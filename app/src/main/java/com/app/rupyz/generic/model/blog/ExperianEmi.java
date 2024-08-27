
package com.app.rupyz.generic.model.blog;

public class ExperianEmi {

    private Integer id;
    private Double scheduled_monthly_payment_amount;
    private String calc_due_date;
    private String subscriber_name;
    private String account_number;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getScheduledMonthlyPaymentAmount() {
        return scheduled_monthly_payment_amount;
    }

    public void setScheduledMonthlyPaymentAmount(Double scheduled_monthly_payment_amount) {
        this.scheduled_monthly_payment_amount = scheduled_monthly_payment_amount;
    }

    public String getMonthDueDay() {
        return calc_due_date;
    }

    public void setMonthDueDay(String calc_due_date) {
        this.calc_due_date = calc_due_date;
    }

    public String getSubscriberName() {
        return subscriber_name;
    }

    public void setSubscriberName(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    public String getAccountNumber() {
        return account_number;
    }

    public void setAccountNumber(String accountNumber) {
        this.account_number = account_number;
    }

}
