
package com.app.rupyz.generic.model.blog;

public class EquifaxEmi {

    private Integer id;
    private String calc_due_date;
    private Integer installment_amount;
    private Integer month_due_day;
    private String institution_name;
    private String account_no;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMonthDueDay() {
        return calc_due_date;
    }

    public void setMonthDueDay(String calc_due_date) {
        this.calc_due_date = calc_due_date;
    }

    public String getCalc_due_date() {
        return calc_due_date;
    }

    public void setCalc_due_date(String calc_due_date) {
        this.calc_due_date = calc_due_date;
    }

    public Integer getInstallment_amount() {
        return installment_amount;
    }

    public void setInstallment_amount(Integer installment_amount) {
        this.installment_amount = installment_amount;
    }

    public Integer getMonth_due_day() {
        return month_due_day;
    }

    public void setMonth_due_day(Integer month_due_day) {
        this.month_due_day = month_due_day;
    }

    public String getInstitution_name() {
        return institution_name;
    }

    public void setInstitution_name(String institution_name) {
        this.institution_name = institution_name;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }
}
