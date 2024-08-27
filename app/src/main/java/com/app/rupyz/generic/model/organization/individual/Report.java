package com.app.rupyz.generic.model.organization.individual;

import java.util.Date;
import java.util.List;

public class Report {
    private int id;
    private String full_name;
    private String score_type;
    private String score_version;
    private Integer score_value;
    private String score_comment;
    private String credit_age;
    private int loans_total_count;
    private int loans_active_count;
    private int loans_closed_count;
    private int overdue_count;
    private double aggregated_overdue_amount;
    private double aggregated_sanctioned_amount;
    private double aggregated_writeoff_amount;
    private int repayments_total_count;
    private int repayments_missed_count;
    private List<EnquiryHistory> enquiry_history;
    private String enq_purpose;
    private int enq_total;
    private int enq_past30days;
    private int enq_past12months;
    private int enq_past24months;
    private List<Tradeline> tradelines;
    private List<Alert> alerts;
    private GraphData graph_data;
    private IndividualAdditionalInfo individual_additional_info;
    private OwnershipMix ownership_mix;
    private FacilityMix facility_mix;
    private int defaults_and_npa_count;
    private double aggregated_defaults_and_npa_amount;
    private int negative_accounts_count;
    private double negative_accounts_amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getScore_type() {
        return score_type;
    }

    public void setScore_type(String score_type) {
        this.score_type = score_type;
    }

    public String getScore_version() {
        return score_version;
    }

    public void setScore_version(String score_version) {
        this.score_version = score_version;
    }

    public Integer getScore_value() {
        return score_value;
    }

    public void setScore_value(Integer score_value) {
        this.score_value = score_value;
    }

    public String getScore_comment() {
        return score_comment;
    }

    public void setScore_comment(String score_comment) {
        this.score_comment = score_comment;
    }

    public String getCredit_age() {
        return credit_age;
    }

    public void setCredit_age(String credit_age) {
        this.credit_age = credit_age;
    }

    public int getLoans_total_count() {
        return loans_total_count;
    }

    public void setLoans_total_count(int loans_total_count) {
        this.loans_total_count = loans_total_count;
    }

    public int getLoans_active_count() {
        return loans_active_count;
    }

    public void setLoans_active_count(int loans_active_count) {
        this.loans_active_count = loans_active_count;
    }

    public int getLoans_closed_count() {
        return loans_closed_count;
    }

    public void setLoans_closed_count(int loans_closed_count) {
        this.loans_closed_count = loans_closed_count;
    }

    public int getOverdue_count() {
        return overdue_count;
    }

    public void setOverdue_count(int overdue_count) {
        this.overdue_count = overdue_count;
    }

    public double getAggregated_overdue_amount() {
        return aggregated_overdue_amount;
    }

    public void setAggregated_overdue_amount(double aggregated_overdue_amount) {
        this.aggregated_overdue_amount = aggregated_overdue_amount;
    }

    public double getAggregated_sanctioned_amount() {
        return aggregated_sanctioned_amount;
    }

    public void setAggregated_sanctioned_amount(double aggregated_sanctioned_amount) {
        this.aggregated_sanctioned_amount = aggregated_sanctioned_amount;
    }

    public double getAggregated_writeoff_amount() {
        return aggregated_writeoff_amount;
    }

    public void setAggregated_writeoff_amount(double aggregated_writeoff_amount) {
        this.aggregated_writeoff_amount = aggregated_writeoff_amount;
    }

    public int getRepayments_total_count() {
        return repayments_total_count;
    }

    public void setRepayments_total_count(int repayments_total_count) {
        this.repayments_total_count = repayments_total_count;
    }

    public int getRepayments_missed_count() {
        return repayments_missed_count;
    }

    public void setRepayments_missed_count(int repayments_missed_count) {
        this.repayments_missed_count = repayments_missed_count;
    }

    public List<EnquiryHistory> getEnquiry_history() {
        return enquiry_history;
    }

    public void setEnquiry_history(List<EnquiryHistory> enquiry_history) {
        this.enquiry_history = enquiry_history;
    }

    public String getEnq_purpose() {
        return enq_purpose;
    }

    public void setEnq_purpose(String enq_purpose) {
        this.enq_purpose = enq_purpose;
    }

    public int getEnq_total() {
        return enq_total;
    }

    public void setEnq_total(int enq_total) {
        this.enq_total = enq_total;
    }

    public int getEnq_past30days() {
        return enq_past30days;
    }

    public void setEnq_past30days(int enq_past30days) {
        this.enq_past30days = enq_past30days;
    }

    public int getEnq_past12months() {
        return enq_past12months;
    }

    public void setEnq_past12months(int enq_past12months) {
        this.enq_past12months = enq_past12months;
    }

    public int getEnq_past24months() {
        return enq_past24months;
    }

    public void setEnq_past24months(int enq_past24months) {
        this.enq_past24months = enq_past24months;
    }

    public List<Tradeline> getTradelines() {
        return tradelines;
    }

    public void setTradelines(List<Tradeline> tradelines) {
        this.tradelines = tradelines;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public GraphData getGraph_data() {
        return graph_data;
    }

    public void setGraph_data(GraphData graph_data) {
        this.graph_data = graph_data;
    }

    public IndividualAdditionalInfo getIndividual_additional_info() {
        return individual_additional_info;
    }

    public void setIndividual_additional_info(IndividualAdditionalInfo individual_additional_info) {
        this.individual_additional_info = individual_additional_info;
    }

    public OwnershipMix getOwnership_mix() {
        return ownership_mix;
    }

    public void setOwnership_mix(OwnershipMix ownership_mix) {
        this.ownership_mix = ownership_mix;
    }

    public FacilityMix getFacility_mix() {
        return facility_mix;
    }

    public void setFacility_mix(FacilityMix facility_mix) {
        this.facility_mix = facility_mix;
    }

    public int getDefaults_and_npa_count() {
        return defaults_and_npa_count;
    }

    public void setDefaults_and_npa_count(int defaults_and_npa_count) {
        this.defaults_and_npa_count = defaults_and_npa_count;
    }

    public double getAggregated_defaults_and_npa_amount() {
        return aggregated_defaults_and_npa_amount;
    }

    public void setAggregated_defaults_and_npa_amount(double aggregated_defaults_and_npa_amount) {
        this.aggregated_defaults_and_npa_amount = aggregated_defaults_and_npa_amount;
    }

    public int getNegative_accounts_count() {
        return negative_accounts_count;
    }

    public void setNegative_accounts_count(int negative_accounts_count) {
        this.negative_accounts_count = negative_accounts_count;
    }

    public double getNegative_accounts_amount() {
        return negative_accounts_amount;
    }

    public void setNegative_accounts_amount(double negative_accounts_amount) {
        this.negative_accounts_amount = negative_accounts_amount;
    }

    public double getBorrower_outstanding_amount() {
        return borrower_outstanding_amount;
    }

    public void setBorrower_outstanding_amount(double borrower_outstanding_amount) {
        this.borrower_outstanding_amount = borrower_outstanding_amount;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getOrganization() {
        return organization;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getRaw_equifax_report() {
        return raw_equifax_report;
    }

    public void setRaw_equifax_report(int raw_equifax_report) {
        this.raw_equifax_report = raw_equifax_report;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public Object getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(Object updated_by) {
        this.updated_by = updated_by;
    }

    private double borrower_outstanding_amount;
    private Date created_at;
    private String updated_at;
    private int organization;
    private int profile;
    private int raw_equifax_report;
    private int created_by;
    private Object updated_by;
}
