package com.app.rupyz.generic.model.organization.individual;

import com.app.rupyz.generic.model.organization.History48MonthsItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Tradelines {

	@SerializedName("closed_date")
	private String closedDate;

	@SerializedName("overdue_amount")
	private String overdueAmount;

	@SerializedName("History48Months")
	private List<History48Months> history48Months;

	@SerializedName("is_overdue")
	private boolean isOverdue;

	@SerializedName("credit_type_group")
	private String creditTypeGroup;

	@SerializedName("account_status")
	private String accountStatus;

	@SerializedName("credit_type")
	private String creditType;

	@SerializedName("account_type")
	private String accountType;

	@SerializedName("current_balance_amount")
	private String currentBalanceAmount;

	@SerializedName("account_no")
	private String accountNo;

	@SerializedName("institution_name")
	private String institutionName;

	@SerializedName("asset_classification")
	private String assetClassification;

	@SerializedName("date_opened")
	private String sanctionDate;

	@SerializedName("sanction_amount")
	private String sanctionAmount;

	@SerializedName("repayments_total")
	private int repaymentsTotal;

	@SerializedName("repayments_missed")
	private int repaymentsMissed;

	@SerializedName("last_reported_date")
	private String lastReportedDate;

	@SerializedName("drawing_power_amount")
	private String drawingPowerAmount;

	@SerializedName("is_missed_repayments")
	private boolean isMissedRepayments;

	@SerializedName("is_negative")
	private boolean isNegative;

	@SerializedName("calc_due_date")
	@Expose
	private String calcDueDate;

	@SerializedName("interest_rate")
	@Expose
	private Double interestRate;

	@SerializedName("repayment_tenure")
	@Expose
	private Integer repaymentTenure;

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("installment_amount")
	@Expose
	private Double installmentAmount;
	@SerializedName("month_due_day")
	@Expose
	private Integer monthDueDay;


	public void setClosedDate(String closedDate){
		this.closedDate = closedDate;
	}

	public String getClosedDate(){
		return closedDate;
	}

	public void setOverdueAmount(String overdueAmount){
		this.overdueAmount = overdueAmount;
	}

	public String getOverdueAmount(){
		return overdueAmount;
	}

	public void setHistory48Months(List<History48Months> history48Months){
		this.history48Months = history48Months;
	}

	public List<History48Months> getHistory48Months(){
		return history48Months;
	}

	public void setIsOverdue(boolean isOverdue){
		this.isOverdue = isOverdue;
	}

	public boolean isIsOverdue(){
		return isOverdue;
	}

	public void setCreditTypeGroup(String creditTypeGroup){
		this.creditTypeGroup = creditTypeGroup;
	}

	public String getCreditTypeGroup(){
		return creditTypeGroup;
	}

	public void setAccountStatus(String accountStatus){
		this.accountStatus = accountStatus;
	}

	public String getAccountStatus(){
		return accountStatus;
	}

	public void setCreditType(String creditType){
		this.creditType = creditType;
	}

	public String getCreditType(){
		return creditType;
	}

	public void setAccountType(String accountType){
		this.accountType = accountType;
	}

	public String getAccountType(){
		return accountType;
	}

	public void setCurrentBalanceAmount(String currentBalanceAmount){
		this.currentBalanceAmount = currentBalanceAmount;
	}

	public String getCurrentBalanceAmount(){
		return currentBalanceAmount;
	}

	public void setAccountNo(String accountNo){
		this.accountNo = accountNo;
	}

	public String getAccountNo(){
		return accountNo;
	}

	public void setInstitutionName(String institutionName){
		this.institutionName = institutionName;
	}

	public String getInstitutionName(){
		return institutionName;
	}

	public void setAssetClassification(String assetClassification){
		this.assetClassification = assetClassification;
	}

	public String getAssetClassification(){
		return assetClassification;
	}

	public void setSanctionDate(String sanctionDate){
		this.sanctionDate = sanctionDate;
	}

	public String getSanctionDate(){
		return sanctionDate;
	}

	public void setSanctionAmount(String sanctionAmount){
		this.sanctionAmount = sanctionAmount;
	}

	public String getSanctionAmount(){
		return sanctionAmount;
	}

	public void setRepaymentsTotal(int repaymentsTotal){
		this.repaymentsTotal = repaymentsTotal;
	}

	public int getRepaymentsTotal(){
		return repaymentsTotal;
	}

	public void setRepaymentsMissed(int repaymentsMissed){
		this.repaymentsMissed = repaymentsMissed;
	}

	public int getRepaymentsMissed(){
		return repaymentsMissed;
	}

	public void setLastReportedDate(String lastReportedDate){
		this.lastReportedDate = lastReportedDate;
	}

	public String getLastReportedDate(){
		return lastReportedDate;
	}

	public void setDrawingPowerAmount(String drawingPowerAmount){
		this.drawingPowerAmount = drawingPowerAmount;
	}

	public String getDrawingPowerAmount(){
		return drawingPowerAmount;
	}

	public void setIsMissedRepayments(boolean isMissedRepayments){
		this.isMissedRepayments = isMissedRepayments;
	}

	public boolean isIsMissedRepayments(){
		return isMissedRepayments;
	}

	public void setIsNegative(boolean isNegative){
		this.isNegative = isNegative;
	}

	public boolean isIsNegative(){
		return isNegative;
	}

	public boolean isOverdue() {
		return isOverdue;
	}

	public void setOverdue(boolean overdue) {
		isOverdue = overdue;
	}

	public boolean isMissedRepayments() {
		return isMissedRepayments;
	}

	public void setMissedRepayments(boolean missedRepayments) {
		isMissedRepayments = missedRepayments;
	}

	public boolean isNegative() {
		return isNegative;
	}

	public void setNegative(boolean negative) {
		isNegative = negative;
	}

	public String getCalcDueDate() {
		return calcDueDate;
	}

	public void setCalcDueDate(String calcDueDate) {
		this.calcDueDate = calcDueDate;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public Integer getRepaymentTenure() {
		return repaymentTenure;
	}

	public void setRepaymentTenure(Integer repaymentTenure) {
		this.repaymentTenure = repaymentTenure;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getInstallmentAmount() {
		return installmentAmount;
	}

	public void setInstallmentAmount(Double installmentAmount) {
		this.installmentAmount = installmentAmount;
	}

	public Integer getMonthDueDay() {
		return monthDueDay;
	}

	public void setMonthDueDay(Integer monthDueDay) {
		this.monthDueDay = monthDueDay;
	}
}