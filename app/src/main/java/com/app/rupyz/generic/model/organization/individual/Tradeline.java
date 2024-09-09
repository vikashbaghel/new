package com.app.rupyz.generic.model.organization.individual;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tradeline {
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public boolean isIs_overdue() {
        return is_overdue;
    }

    public void setIs_overdue(boolean is_overdue) {
        this.is_overdue = is_overdue;
    }

    public String getDate_closed() {
        return date_closed;
    }

    public void setDate_closed(String date_closed) {
        this.date_closed = date_closed;
    }

    public String getDate_opened() {
        return date_opened;
    }

    public void setDate_opened(String date_opened) {
        this.date_opened = date_opened;
    }

    public boolean isIs_negative() {
        return is_negative;
    }

    public void setIs_negative(boolean is_negative) {
        this.is_negative = is_negative;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
    }

    public String getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(String interest_rate) {
        this.interest_rate = interest_rate;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }

    public int getOverdue_amount() {
        return overdue_amount;
    }

    public void setOverdue_amount(int overdue_amount) {
        this.overdue_amount = overdue_amount;
    }

    public String getOwnership_type() {
        return ownership_type;
    }

    public void setOwnership_type(String ownership_type) {
        this.ownership_type = ownership_type;
    }

    public String getTerm_frequency() {
        return term_frequency;
    }

    public void setTerm_frequency(String term_frequency) {
        this.term_frequency = term_frequency;
    }

    public List<History48Months> getHistory48Months() {
        return history48Months;
    }

    public void setHistory48Months(List<History48Months> history48Months) {
        this.history48Months = history48Months;
    }

    public String getCollateral_type() {
        return collateral_type;
    }

    public void setCollateral_type(String collateral_type) {
        this.collateral_type = collateral_type;
    }

    public int getSanction_amount() {
        return sanction_amount;
    }

    public void setSanction_amount(int sanction_amount) {
        this.sanction_amount = sanction_amount;
    }

    public String getCollateral_value() {
        return collateral_value;
    }

    public void setCollateral_value(String collateral_value) {
        this.collateral_value = collateral_value;
    }

    public String getInstitution_name() {
        return institution_name;
    }

    public void setInstitution_name(String institution_name) {
        this.institution_name = institution_name;
    }

    public String getRepayment_tenure() {
        return repayment_tenure;
    }

    public void setRepayment_tenure(String repayment_tenure) {
        this.repayment_tenure = repayment_tenure;
    }

    public int getRepayments_total() {
        return repayments_total;
    }

    public void setRepayments_total(int repayments_total) {
        this.repayments_total = repayments_total;
    }

    public int getWrite_off_amount() {
        return write_off_amount;
    }

    public void setWrite_off_amount(int write_off_amount) {
        this.write_off_amount = write_off_amount;
    }

    public String getDate_last_payment() {
        return date_last_payment;
    }

    public void setDate_last_payment(String date_last_payment) {
        this.date_last_payment = date_last_payment;
    }

    public int getRepayments_missed() {
        return repayments_missed;
    }

    public void setRepayments_missed(int repayments_missed) {
        this.repayments_missed = repayments_missed;
    }

    public String getDate_last_reported() {
        return date_last_reported;
    }

    public void setDate_last_reported(String date_last_reported) {
        this.date_last_reported = date_last_reported;
    }

    public int getHigh_credit_amount() {
        return high_credit_amount;
    }

    public void setHigh_credit_amount(int high_credit_amount) {
        this.high_credit_amount = high_credit_amount;
    }

    public Double getInstallment_amount() {
        return installment_amount;
    }

    public void setInstallment_amount(Double installment_amount) {
        this.installment_amount = installment_amount;
    }

    public int getCredit_limit_amount() {
        return credit_limit_amount;
    }

    public void setCredit_limit_amount(int credit_limit_amount) {
        this.credit_limit_amount = credit_limit_amount;
    }

    public int getLast_payment_amount() {
        return last_payment_amount;
    }

    public void setLast_payment_amount(int last_payment_amount) {
        this.last_payment_amount = last_payment_amount;
    }

    public String getAsset_classification() {
        return asset_classification;
    }

    public void setAsset_classification(String asset_classification) {
        this.asset_classification = asset_classification;
    }

    public boolean isIs_missed_repayments() {
        return is_missed_repayments;
    }

    public void setIs_missed_repayments(boolean is_missed_repayments) {
        this.is_missed_repayments = is_missed_repayments;
    }

    public int getCurrent_balance_amount() {
        return current_balance_amount;
    }

    public void setCurrent_balance_amount(int current_balance_amount) {
        this.current_balance_amount = current_balance_amount;
    }

    private String reason;
    private String source;
    private String account_no;
    private boolean is_overdue;
    private String date_closed;
    private String date_opened;
    private boolean is_negative;
    private String account_type;
    private String interest_rate;
    private String account_status;
    private int overdue_amount;
    private String ownership_type;
    private String term_frequency;
    @SerializedName("History48Months")
    private List<History48Months> history48Months;
    private String collateral_type;
    private int sanction_amount;
    private String collateral_value;
    private String institution_name;
    private String repayment_tenure;
    private int repayments_total;
    private int write_off_amount;
    private String date_last_payment;
    private int repayments_missed;
    private String date_last_reported;
    private int high_credit_amount;
    private Double installment_amount;
    private int credit_limit_amount;
    private int last_payment_amount;
    private String asset_classification;
    private boolean is_missed_repayments;
    private int current_balance_amount;
    private Integer month_due_day;

    public Integer getMonth_due_day() {
        return month_due_day;
    }

    public void setMonth_due_day(Integer month_due_day) {
        this.month_due_day = month_due_day;
    }
}
