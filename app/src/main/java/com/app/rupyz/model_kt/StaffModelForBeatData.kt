package com.app.rupyz.model_kt

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class StaffModelForBeatData(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("is_selected")
	val isSelected: Boolean? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("roles")
	val roles: List<String>? = null,

	@field:SerializedName("profile_pic_url")
	val profilePicUrl: String? = null
) : Parcelable
