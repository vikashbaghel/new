package com.app.rupyz.generic.model.organization;

import com.google.gson.annotations.SerializedName;

public class AlertsItem{

	@SerializedName("title")
	private String title;

	@SerializedName("message")
	private String message;

	@SerializedName("credit_type")
	private String creditType;

	@SerializedName("account_no")
	private String accountNo;

	@SerializedName("institution_name")
	private String institutionName;

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setCreditType(String creditType){
		this.creditType = creditType;
	}

	public String getCreditType(){
		return creditType;
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
}