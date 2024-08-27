package com.app.rupyz.model_kt

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrganizationInfoModel(

	@field:SerializedName("nanoid")
	val nanoid: String? = null,

	@field:SerializedName("website")
	val website: String? = null,

	@field:SerializedName("equifax_score")
	val equifaxScore: Int? = null,

	@field:SerializedName("selected_authorized_signatory")
	val selectedAuthorizedSignatory: String? = null,

	@field:SerializedName("pan_id")
	val panId: String? = null,

	@field:SerializedName("primary_gstin")
	val primaryGstin: String? = null,

	@field:SerializedName("reg_step")
	val regStep: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("legal_name")
	val legalName: String? = null,

	@field:SerializedName("logo_image_url")
	val logoImageUrl: String? = null,

	@field:SerializedName("created_by")
	val createdBy: Int? = null,

	@field:SerializedName("slug")
	val slug: String? = null,

	var isSelected: Boolean? = null

) : Parcelable {
	companion object {
		lateinit var CREATOR: Parcelable.Creator<OrganizationInfoModel>
	}
}
