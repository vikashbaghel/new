package com.app.rupyz.generic.model.organization;

import com.google.gson.annotations.SerializedName;

public class LenderMix{

	@SerializedName("Others")
	private Others others;

	@SerializedName("NBFC")
	private NBFC nBFC;

	@SerializedName("PSU bank")
	private PSUBank pSUBank;

	@SerializedName("Private Bank")
	private PrivateBank privateBank;

	public void setOthers(Others others){
		this.others = others;
	}

	public Others getOthers(){
		return others;
	}

	public void setNBFC(NBFC nBFC){
		this.nBFC = nBFC;
	}

	public NBFC getNBFC(){
		return nBFC;
	}

	public void setPSUBank(PSUBank pSUBank){
		this.pSUBank = pSUBank;
	}

	public PSUBank getPSUBank(){
		return pSUBank;
	}

	public void setPrivateBank(PrivateBank privateBank){
		this.privateBank = privateBank;
	}

	public PrivateBank getPrivateBank(){
		return privateBank;
	}
}