package com.app.rupyz.generic.model.organization;

import com.google.gson.annotations.SerializedName;

public class PrivateBank{

	@SerializedName("overdue_amount")
	private int overdueAmount;

	@SerializedName("sanctioned_amount")
	private int sanctionedAmount;

	@SerializedName("count")
	private int count;

	@SerializedName("current_balance")
	private int currentBalance;

	public void setOverdueAmount(int overdueAmount){
		this.overdueAmount = overdueAmount;
	}

	public int getOverdueAmount(){
		return overdueAmount;
	}

	public void setSanctionedAmount(int sanctionedAmount){
		this.sanctionedAmount = sanctionedAmount;
	}

	public int getSanctionedAmount(){
		return sanctionedAmount;
	}

	public void setCount(int count){
		this.count = count;
	}

	public int getCount(){
		return count;
	}

	public void setCurrentBalance(int currentBalance){
		this.currentBalance = currentBalance;
	}

	public int getCurrentBalance(){
		return currentBalance;
	}
}