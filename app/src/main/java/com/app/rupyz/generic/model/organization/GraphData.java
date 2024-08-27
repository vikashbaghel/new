package com.app.rupyz.generic.model.organization;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class GraphData{

	@SerializedName("date")
	private List<String> date;

	@SerializedName("sanction_amount")
	private List<Integer> sanctionAmount;

	@SerializedName("current_balance")
	private List<Integer> currentBalance;

	@SerializedName("open_CF")
	private List<Integer> openCF;

	public void setDate(List<String> date){
		this.date = date;
	}

	public List<String> getDate(){
		return date;
	}

	public void setSanctionAmount(List<Integer> sanctionAmount){
		this.sanctionAmount = sanctionAmount;
	}

	public List<Integer> getSanctionAmount(){
		return sanctionAmount;
	}

	public void setCurrentBalance(List<Integer> currentBalance){
		this.currentBalance = currentBalance;
	}

	public List<Integer> getCurrentBalance(){
		return currentBalance;
	}

	public void setOpenCF(List<Integer> openCF){
		this.openCF = openCF;
	}

	public List<Integer> getOpenCF(){
		return openCF;
	}
}