package com.app.rupyz.generic.model.individual.experian;

import java.util.List;

public class ExperianInfoModel {
    public int getReport_pause_days() {
        return report_pause_days;
    }

    public void setReport_pause_days(int report_pause_days) {
        this.report_pause_days = report_pause_days;
    }

    private int report_pause_days;
    private int id;
    private RelationshipDetails relationship_details;

    public TotalCapsData getTotal_caps_data() {
        return total_caps_data;
    }

    public void setTotal_caps_data(TotalCapsData total_caps_data) {
        this.total_caps_data = total_caps_data;
    }

    private TotalCapsData total_caps_data;
    private int score_value;
    private String score_comment;
    private List<Tradeline> tradelines;

    public GraphData getGraph_data() {
        return graph_data;
    }

    public void setGraph_data(GraphData graph_data) {
        this.graph_data = graph_data;
    }

    private GraphData graph_data;

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    private List<Alert> alerts;
    private int negative_accounts_count;
    private String negative_accounts_amount;
    private int loans_active_count;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    private String updated_at;
    private int loans_closed_count;

    public String getCredit_age() {
        return credit_age;
    }

    public void setCredit_age(String credit_age) {
        this.credit_age = credit_age;
    }

    private String credit_age;
    private int loans_total_count;
    private int loans_default_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RelationshipDetails getRelationship_details() {
        return relationship_details;
    }

    public void setRelationship_details(RelationshipDetails relationship_details) {
        this.relationship_details = relationship_details;
    }

    public int getScore_value() {
        return score_value;
    }

    public void setScore_value(int score_value) {
        this.score_value = score_value;
    }

    public String getScore_comment() {
        return score_comment;
    }

    public void setScore_comment(String score_comment) {
        this.score_comment = score_comment;
    }

    public List<Tradeline> getTradelines() {
        return tradelines;
    }

    public void setTradelines(List<Tradeline> tradelines) {
        this.tradelines = tradelines;
    }

    public int getNegative_accounts_count() {
        return negative_accounts_count;
    }

    public void setNegative_accounts_count(int negative_accounts_count) {
        this.negative_accounts_count = negative_accounts_count;
    }

    public String getNegative_accounts_amount() {
        return negative_accounts_amount;
    }

    public void setNegative_accounts_amount(String negative_accounts_amount) {
        this.negative_accounts_amount = negative_accounts_amount;
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

    public int getLoans_total_count() {
        return loans_total_count;
    }

    public void setLoans_total_count(int loans_total_count) {
        this.loans_total_count = loans_total_count;
    }

    public int getLoans_default_count() {
        return loans_default_count;
    }

    public void setLoans_default_count(int loans_default_count) {
        this.loans_default_count = loans_default_count;
    }


    public int getRepayments_missed_count() {
        return repayments_missed_count;
    }

    public void setRepayments_missed_count(int repayments_missed_count) {
        this.repayments_missed_count = repayments_missed_count;
    }

    public String getBorrower_outstanding_amount() {
        return borrower_outstanding_amount;
    }

    public void setBorrower_outstanding_amount(String borrower_outstanding_amount) {
        this.borrower_outstanding_amount = borrower_outstanding_amount;
    }

    public int getAggregated_sanctioned_amount() {
        return aggregated_sanctioned_amount;
    }

    public void setAggregated_sanctioned_amount(int aggregated_sanctioned_amount) {
        this.aggregated_sanctioned_amount = aggregated_sanctioned_amount;
    }

    public int getSuit_case_amount() {
        return suit_case_amount;
    }

    public void setSuit_case_amount(int suit_case_amount) {
        this.suit_case_amount = suit_case_amount;
    }

    public int getSuit_case_count() {
        return suit_case_count;
    }

    public void setSuit_case_count(int suit_case_count) {
        this.suit_case_count = suit_case_count;
    }

    public int getOverdue_count() {
        return overdue_count;
    }

    public void setOverdue_count(int overdue_count) {
        this.overdue_count = overdue_count;
    }

    public int getOverdue_amount() {
        return overdue_amount;
    }

    public void setOverdue_amount(int overdue_amount) {
        this.overdue_amount = overdue_amount;
    }

    public Ownership getOwnership() {
        return ownership;
    }

    public void setOwnership(Ownership ownership) {
        this.ownership = ownership;
    }

    public FacilityMix getFacility_mix() {
        return facility_mix;
    }

    public void setFacility_mix(FacilityMix facility_mix) {
        this.facility_mix = facility_mix;
    }

    public DefaultsAndNpa getDefaults_and_npa() {
        return defaults_and_npa;
    }

    public void setDefaults_and_npa(DefaultsAndNpa defaults_and_npa) {
        this.defaults_and_npa = defaults_and_npa;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public int getRepayments_ontime_count() {
        return repayments_ontime_count;
    }

    public void setRepayments_ontime_count(int repayments_ontime_count) {
        this.repayments_ontime_count = repayments_ontime_count;
    }

    private int repayments_ontime_count;
    private int repayments_missed_count;
    private String borrower_outstanding_amount;
    private int aggregated_sanctioned_amount;
    private int suit_case_amount;
    private int suit_case_count;
    private int overdue_count;
    private int overdue_amount;
    private Ownership ownership;
    private FacilityMix facility_mix;
    private DefaultsAndNpa defaults_and_npa;
    private int profile;
    private int created_by;
    private String updated_by;
}
