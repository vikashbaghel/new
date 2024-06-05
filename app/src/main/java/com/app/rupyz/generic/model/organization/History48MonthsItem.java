package com.app.rupyz.generic.model.organization;

import com.google.gson.annotations.SerializedName;

public class History48MonthsItem{

	@SerializedName("month")
	private int month;

	@SerializedName("current_balance_limit_utilized_marktomarket")
	private String currentBalanceLimitUtilizedMarktomarket;

	@SerializedName("year")
	private int year;

	@SerializedName("assetclassification_dayspastdue")
	private String assetclassificationDayspastdue;

	@SerializedName("is_missed")
	private boolean isMissed;

	public void setMonth(int month){
		this.month = month;
	}

	public int getMonth(){
		return month;
	}

	public void setCurrentBalanceLimitUtilizedMarktomarket(String currentBalanceLimitUtilizedMarktomarket){
		this.currentBalanceLimitUtilizedMarktomarket = currentBalanceLimitUtilizedMarktomarket;
	}

	public String getCurrentBalanceLimitUtilizedMarktomarket(){
		return currentBalanceLimitUtilizedMarktomarket;
	}

	public void setYear(int year){
		this.year = year;
	}

	public int getYear(){
		return year;
	}

	public void setAssetclassificationDayspastdue(String assetclassificationDayspastdue){
		this.assetclassificationDayspastdue = assetclassificationDayspastdue;
	}

	public String getAssetclassificationDayspastdue(){
		return assetclassificationDayspastdue;
	}

	public boolean isMissed() {
		return isMissed;
	}

	public void setMissed(boolean missed) {
		isMissed = missed;
	}
}