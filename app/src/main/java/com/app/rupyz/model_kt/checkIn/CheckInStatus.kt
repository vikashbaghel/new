package com.app.rupyz.model_kt.checkIn

import com.google.gson.annotations.SerializedName

data class CheckInStatus (
	
	@SerializedName("data"    ) var data    : Data?    = Data(),
	@SerializedName("message" ) var message : String?  = null,
	@SerializedName("error"   ) var error   : Boolean? = null
                         ){
	
	data class Data (
			
			@SerializedName("customer_id"   ) var customerId   : Int?     = null,
			@SerializedName("customer_name" ) var customerName : String?  = null,
			@SerializedName("is_checked_in" ) var isCheckedIn  : Boolean? = null
	                
	                )
}