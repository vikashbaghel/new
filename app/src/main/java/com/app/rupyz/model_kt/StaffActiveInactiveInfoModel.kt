package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class StaffActiveResponseModel(

	@field:SerializedName("data")
	val data: ArrayList<StaffActiveInactiveInfoModel>? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("error")
	var error: Boolean? = null
) : Parcelable

@Parcelize
data class StaffActiveInactiveInfoModel(

	@field:SerializedName("staff_name")
	val staffName: String? = null,

	@field:SerializedName("staff_id")
	val staffId: Int? = null,

	@field:SerializedName("time_in")
	val timeIn: String? = null,

	@field:SerializedName("pic_url")
	val picUrl: String? = null
) : Parcelable
