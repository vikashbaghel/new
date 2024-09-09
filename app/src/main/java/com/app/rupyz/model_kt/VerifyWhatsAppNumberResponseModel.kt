package com.app.rupyz.model_kt

import com.google.gson.annotations.SerializedName

data class VerifyWhatsAppNumberResponseModel(
		
		@field:SerializedName("message") var message : String? = null,
		
		@field:SerializedName("error") var error : Boolean? = null,
		
		@field:SerializedName("data") var data : Data? = Data(),
		
		
		) {
	
	data class Data(
			
			@SerializedName("is_used") var isUsed : Boolean? = null
	               
	               )
}