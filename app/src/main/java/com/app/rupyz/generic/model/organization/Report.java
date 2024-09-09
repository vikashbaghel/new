package com.app.rupyz.generic.model.organization;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Report {

    @SerializedName("lender_mix")
    private LenderMix lenderMix;

    @SerializedName("negative_accounts_count")
    private int negativeAccountsCount;

    @SerializedName("enq_past30days")
    private int enqPast30days;

    public List<String> getNature_of_business() {
        return nature_of_business;
    }

    public void setNature_of_business(List<String> nature_of_business) {
        this.nature_of_business = nature_of_business;
    }

    public String getIncorporation_date() {
        return incorporation_date;
    }

    public void setIncorporation_date(String incorporation_date) {
        this.incorporation_date = incorporation_date;
    }

    private List<String> nature_of_business;
    private String incorporation_date;

    @SerializedName("enq_past12months")
    private int enqPast12months;

    @SerializedName("aggregated_sanctioned_amount")
    private String aggregatedSanctionedAmount;

    @SerializedName("graph_data")
    private GraphData graphData;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("loans_total_count")
    private int loansTotalCount;

    @SerializedName("score_comment")
    private String scoreComment;

    @SerializedName("raw_equifax_report")
    private int rawEquifaxReport;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("aggregated_writeoff_amount")
    private String aggregatedWriteoffAmount;

    @SerializedName("score_version")
    private Object scoreVersion;

    @SerializedName("negative_accounts_amount")
    private String negativeAccountsAmount;

    @SerializedName("id")
    private int id;

    @SerializedName("legal_name")
    private String legalName;

    public String getAuthorizedSignatory() {
        return authorizedSignatory;
    }

    public void setAuthorizedSignatory(String authorizedSignatory) {
        this.authorizedSignatory = authorizedSignatory;
    }

    @SerializedName("authorized_signatory")
    private String authorizedSignatory;


    @SerializedName("credit_age")
    private String creditAge;

    @SerializedName("days_remaining")
    private int daysRemaining;

    @SerializedName("facility_mix")
    private FacilityMix facilityMix;

    @SerializedName("tradelines")
    private List<TradelinesItem> tradelines;

    @SerializedName("aggregated_suit_amount")
    private String aggregatedSuitAmount;

    @SerializedName("repayments_total_count")
    private int repaymentsTotalCount;

    @SerializedName("created_by")
    private int createdBy;

    @SerializedName("overdue_count")
    private int overdueCount;

    @SerializedName("enq_purpose")
    private String enqPurpose;

    @SerializedName("aggregated_defaults_and_npa_amount")
    private String aggregatedDefaultsAndNpaAmount;

    @SerializedName("score_type")
    private String scoreType;

    @SerializedName("loans_active_count")
    private int loansActiveCount;

    @SerializedName("alerts")
    private List<AlertsItem> alerts;

    @SerializedName("defaults_and_npa_count")
    private int defaultsAndNpaCount;

    @SerializedName("borrower_outstanding_amount")
    private String borrowerOutstandingAmount;

    @SerializedName("loans_closed_count")
    private int loansClosedCount;

    @SerializedName("enq_past24months")
    private int enqPast24months;

    @SerializedName("score_value")
    private Integer scoreValue;

    @SerializedName("organization")
    private int organization;

    @SerializedName("updated_by")
    private Object updatedBy;

    @SerializedName("enq_total")
    private int enqTotal;

    @SerializedName("repayments_missed_count")
    private int repaymentsMissedCount;

    @SerializedName("ref_uuid")
    private String refUuid;

    @SerializedName("aggregated_overdue_amount")
    private double aggregatedOverdueAmount;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPan_id() {
        return pan_id;
    }

    public void setPan_id(String pan_id) {
        this.pan_id = pan_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("pan_id")
    private String pan_id;

    @SerializedName("email")
    private String email;

    public void setLenderMix(LenderMix lenderMix) {
        this.lenderMix = lenderMix;
    }

    public LenderMix getLenderMix() {
        return lenderMix;
    }

    public void setNegativeAccountsCount(int negativeAccountsCount) {
        this.negativeAccountsCount = negativeAccountsCount;
    }

    public int getNegativeAccountsCount() {
        return negativeAccountsCount;
    }

    public void setEnqPast30days(int enqPast30days) {
        this.enqPast30days = enqPast30days;
    }

    public int getEnqPast30days() {
        return enqPast30days;
    }

    public void setEnqPast12months(int enqPast12months) {
        this.enqPast12months = enqPast12months;
    }

    public int getEnqPast12months() {
        return enqPast12months;
    }

    public void setAggregatedSanctionedAmount(String aggregatedSanctionedAmount) {
        this.aggregatedSanctionedAmount = aggregatedSanctionedAmount;
    }

    public String getAggregatedSanctionedAmount() {
        return aggregatedSanctionedAmount;
    }

    public void setGraphData(GraphData graphData) {
        this.graphData = graphData;
    }

    public GraphData getGraphData() {
        return graphData;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setLoansTotalCount(int loansTotalCount) {
        this.loansTotalCount = loansTotalCount;
    }

    public int getLoansTotalCount() {
        return loansTotalCount;
    }

    public void setScoreComment(String scoreComment) {
        this.scoreComment = scoreComment;
    }

    public String getScoreComment() {
        return scoreComment;
    }

    public void setRawEquifaxReport(int rawEquifaxReport) {
        this.rawEquifaxReport = rawEquifaxReport;
    }

    public int getRawEquifaxReport() {
        return rawEquifaxReport;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setAggregatedWriteoffAmount(String aggregatedWriteoffAmount) {
        this.aggregatedWriteoffAmount = aggregatedWriteoffAmount;
    }

    public String getAggregatedWriteoffAmount() {
        return aggregatedWriteoffAmount;
    }

    public void setScoreVersion(Object scoreVersion) {
        this.scoreVersion = scoreVersion;
    }

    public Object getScoreVersion() {
        return scoreVersion;
    }

    public void setNegativeAccountsAmount(String negativeAccountsAmount) {
        this.negativeAccountsAmount = negativeAccountsAmount;
    }

    public String getNegativeAccountsAmount() {
        return negativeAccountsAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getLegalName() {
        return legalName;
    }


    public void setCreditAge(String creditAge) {
        this.creditAge = creditAge;
    }

    public String getCreditAge() {
        return creditAge;
    }

    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public int getDaysRemaining() {
        return daysRemaining;
    }

    public void setFacilityMix(FacilityMix facilityMix) {
        this.facilityMix = facilityMix;
    }

    public FacilityMix getFacilityMix() {
        return facilityMix;
    }

    public void setTradelines(List<TradelinesItem> tradelines) {
        this.tradelines = tradelines;
    }

    public List<TradelinesItem> getTradelines() {
        return tradelines;
    }

    public void setAggregatedSuitAmount(String aggregatedSuitAmount) {
        this.aggregatedSuitAmount = aggregatedSuitAmount;
    }

    public String getAggregatedSuitAmount() {
        return aggregatedSuitAmount;
    }

    public void setRepaymentsTotalCount(int repaymentsTotalCount) {
        this.repaymentsTotalCount = repaymentsTotalCount;
    }

    public int getRepaymentsTotalCount() {
        return repaymentsTotalCount;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setOverdueCount(int overdueCount) {
        this.overdueCount = overdueCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public void setEnqPurpose(String enqPurpose) {
        this.enqPurpose = enqPurpose;
    }

    public String getEnqPurpose() {
        return enqPurpose;
    }

    public void setAggregatedDefaultsAndNpaAmount(String aggregatedDefaultsAndNpaAmount) {
        this.aggregatedDefaultsAndNpaAmount = aggregatedDefaultsAndNpaAmount;
    }

    public String getAggregatedDefaultsAndNpaAmount() {
        return aggregatedDefaultsAndNpaAmount;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setLoansActiveCount(int loansActiveCount) {
        this.loansActiveCount = loansActiveCount;
    }

    public int getLoansActiveCount() {
        return loansActiveCount;
    }

    public void setAlerts(List<AlertsItem> alerts) {
        this.alerts = alerts;
    }

    public List<AlertsItem> getAlerts() {
        return alerts;
    }

    public void setDefaultsAndNpaCount(int defaultsAndNpaCount) {
        this.defaultsAndNpaCount = defaultsAndNpaCount;
    }

    public int getDefaultsAndNpaCount() {
        return defaultsAndNpaCount;
    }

    public void setBorrowerOutstandingAmount(String borrowerOutstandingAmount) {
        this.borrowerOutstandingAmount = borrowerOutstandingAmount;
    }

    public String getBorrowerOutstandingAmount() {
        return borrowerOutstandingAmount;
    }

    public void setLoansClosedCount(int loansClosedCount) {
        this.loansClosedCount = loansClosedCount;
    }

    public int getLoansClosedCount() {
        return loansClosedCount;
    }

    public void setEnqPast24months(int enqPast24months) {
        this.enqPast24months = enqPast24months;
    }

    public int getEnqPast24months() {
        return enqPast24months;
    }

    public void setScoreValue(Integer scoreValue) {
        this.scoreValue = scoreValue;
    }

    public Integer getScoreValue() {
        return scoreValue;
    }

    public void setOrganization(int organization) {
        this.organization = organization;
    }

    public int getOrganization() {
        return organization;
    }

    public void setUpdatedBy(Object updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Object getUpdatedBy() {
        return updatedBy;
    }

    public void setEnqTotal(int enqTotal) {
        this.enqTotal = enqTotal;
    }

    public int getEnqTotal() {
        return enqTotal;
    }

    public void setRepaymentsMissedCount(int repaymentsMissedCount) {
        this.repaymentsMissedCount = repaymentsMissedCount;
    }

    public int getRepaymentsMissedCount() {
        return repaymentsMissedCount;
    }

    public void setRefUuid(String refUuid) {
        this.refUuid = refUuid;
    }

    public String getRefUuid() {
        return refUuid;
    }

    public void setAggregatedOverdueAmount(double aggregatedOverdueAmount) {
        this.aggregatedOverdueAmount = aggregatedOverdueAmount;
    }

    public double getAggregatedOverdueAmount() {
        return aggregatedOverdueAmount;
    }
}
