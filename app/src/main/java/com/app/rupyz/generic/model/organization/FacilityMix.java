package com.app.rupyz.generic.model.organization;

import com.app.rupyz.generic.model.organization.individual.AmountForex;
import com.app.rupyz.generic.model.organization.individual.AmountNonfundbased;
import com.app.rupyz.generic.model.organization.individual.AmountOthers;
import com.app.rupyz.generic.model.organization.individual.AmountTermloans;
import com.app.rupyz.generic.model.organization.individual.AmountWorkingcapital;
import com.google.gson.annotations.SerializedName;

public class FacilityMix{


	@SerializedName("Amount Others")
	private AmountOthers amountOthers;

	@SerializedName("Amount Forex")
	private AmountForex amountForex;

	@SerializedName("Amount Term loans")
	private AmountTermloans amountTermloans;

	@SerializedName("Amount Non fund based")
	private AmountNonfundbased amountNonfundbased;

	@SerializedName("Amount Working capital")
	private AmountWorkingcapital amountWorkingcapital;

	@SerializedName("Working capital")
	private Integer workingCapital;

	@SerializedName("Others")
	private Integer others;

	@SerializedName("Forex")
	private Integer forex;

	@SerializedName("Term loans")
	private Integer termLoans;

	@SerializedName("Non fund based")
	private Integer nonFundBased;

	public AmountOthers getAmountOthers() {
		return amountOthers;
	}

	public void setAmountOthers(AmountOthers amountOthers) {
		this.amountOthers = amountOthers;
	}

	public AmountForex getAmountForex() {
		return amountForex;
	}

	public void setAmountForex(AmountForex amountForex) {
		this.amountForex = amountForex;
	}

	public AmountTermloans getAmountTermloans() {
		return amountTermloans;
	}

	public void setAmountTermloans(AmountTermloans amountTermloans) {
		this.amountTermloans = amountTermloans;
	}

	public AmountNonfundbased getAmountNonfundbased() {
		return amountNonfundbased;
	}

	public void setAmountNonfundbased(AmountNonfundbased amountNonfundbased) {
		this.amountNonfundbased = amountNonfundbased;
	}

	public AmountWorkingcapital getAmountWorkingcapital() {
		return amountWorkingcapital;
	}

	public void setAmountWorkingcapital(AmountWorkingcapital amountWorkingcapital) {
		this.amountWorkingcapital = amountWorkingcapital;
	}

	public void setWorkingCapital(Integer workingCapital){
		this.workingCapital = workingCapital;
	}

	public Integer getWorkingCapital(){
		return workingCapital;
	}

	public void setOthers(Integer others){
		this.others = others;
	}

	public Integer getOthers(){
		return others;
	}

	public void setForex(Integer forex){
		this.forex = forex;
	}

	public Integer getForex(){
		return forex;
	}

	public void setTermLoans(Integer termLoans){
		this.termLoans = termLoans;
	}

	public Integer getTermLoans(){
		return termLoans;
	}

	public void setNonFundBased(Integer nonFundBased){
		this.nonFundBased = nonFundBased;
	}

	public Integer getNonFundBased(){
		return nonFundBased;
	}
}